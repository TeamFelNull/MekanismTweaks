package dev.felnull.mekanismtweaks.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Changes Mekanism 9.1.1 by inserting calls to dev.felnull.mekanismtweaks.Hooks (or ClientHooks).
 * <p>
 * Only two kinds of changes are made, so that the existing stack map frames stay valid without recomputing them:
 * a whole method body is replaced by a call to a hook, or a call to a hook that has no branch is inserted.
 */
public class MekanismTweaksTransformer implements IClassTransformer, Opcodes {

    private static final String HOOKS = "dev/felnull/mekanismtweaks/Hooks";
    private static final String CLIENT_HOOKS = "dev/felnull/mekanismtweaks/ClientHooks";
    private static final String RERUN = "dev/felnull/mekanismtweaks/IRerun";

    private static final String UTILS = "mekanism/common/util/MekanismUtils";
    private static final String UPGRADE = "mekanism/common/Upgrade";
    private static final String UPGRADE_TILE = "mekanism/common/base/IUpgradeTile";

    /**
     * The fields that hold the energy and gas per tick, or per operation. They read zero while performing excess operations.
     */
    private static final Set<String> GUARDED_FIELDS = new HashSet<String>(Arrays.asList(
            "energyPerTick", "energyUsage", "secondaryEnergyPerTick", "injectUsage"));

    /**
     * The machines that can perform more than one operation per tick.
     */
    private static final Set<String> MACHINES = new HashSet<String>(Arrays.asList(
            "mekanism.common.tile.TileEntityElectricMachine",
            "mekanism.common.tile.TileEntityAdvancedElectricMachine",
            "mekanism.common.tile.TileEntityChanceMachine",
            "mekanism.common.tile.TileEntityPRC",
            "mekanism.common.tile.TileEntityMetallurgicInfuser",
            "mekanism.common.tile.TileEntityChemicalCrystallizer",
            "mekanism.common.tile.TileEntityChemicalOxidizer",
            "mekanism.common.tile.TileEntityChemicalDissolutionChamber",
            "mekanism.common.tile.TileEntityDigitalMiner",
            "mekanism.common.tile.TileEntityElectricPump"));

    /**
     * The classes that have their own recalculateUpgradables.
     */
    private static final Set<String> RECALCULATING = new HashSet<String>(Arrays.asList(
            "mekanism.common.tile.TileEntityBasicMachine",
            "mekanism.common.tile.TileEntityAdvancedElectricMachine",
            "mekanism.common.tile.TileEntityMetallurgicInfuser",
            "mekanism.common.tile.TileEntityChemicalCrystallizer",
            "mekanism.common.tile.TileEntityChemicalOxidizer",
            "mekanism.common.tile.TileEntityChemicalDissolutionChamber",
            "mekanism.common.tile.TileEntityElectricPump"));

    /**
     * The classes that have their own getScaledProgress.
     */
    private static final Set<String> PROGRESS = new HashSet<String>(Arrays.asList(
            "mekanism.common.tile.TileEntityBasicMachine",
            "mekanism.common.tile.TileEntityMetallurgicInfuser",
            "mekanism.common.tile.TileEntityChemicalCrystallizer",
            "mekanism.common.tile.TileEntityChemicalOxidizer",
            "mekanism.common.tile.TileEntityChemicalDissolutionChamber"));

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null || !transformedName.startsWith("mekanism.")) return bytes;

        boolean utils = transformedName.equals("mekanism.common.util.MekanismUtils");
        boolean upgrade = transformedName.equals("mekanism.common.Upgrade");
        boolean statUtils = transformedName.equals("mekanism.common.util.StatUtils");
        boolean itemUpgrade = transformedName.equals("mekanism.common.item.ItemUpgrade");
        boolean component = transformedName.equals("mekanism.common.tile.component.TileComponentUpgrade");
        boolean gui = transformedName.equals("mekanism.client.gui.element.GuiUpgradeTab");
        boolean machine = MACHINES.contains(transformedName);
        boolean recalculating = RECALCULATING.contains(transformedName);
        boolean progress = PROGRESS.contains(transformedName);
        if (!(utils || upgrade || statUtils || itemUpgrade || component || gui || machine || recalculating || progress)) return bytes;

        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);

        if (utils) transformUtils(node);
        if (upgrade) transformUpgrade(node);
        if (statUtils) transformStatUtils(node);
        if (itemUpgrade) transformItemUpgrade(node);
        if (component) transformComponent(node);
        if (gui) transformGui(node);
        if (machine) transformMachine(node);
        if (recalculating) transformRecalculating(node);
        if (progress) transformProgress(node);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    // ---- MekanismUtils ----

    private void transformUtils(ClassNode node) {
        replaceWithStaticCall(node, "fractionUpgrades", "(L" + UPGRADE_TILE + ";L" + UPGRADE + ";)F", "fractionUpgrades",
                new int[]{ALOAD, ALOAD}, FRETURN);
        replaceWithStaticCall(node, "getTicks", "(L" + UPGRADE_TILE + ";I)I", "getTicks",
                new int[]{ALOAD, ILOAD}, IRETURN);
        replaceWithStaticCall(node, "getEnergyPerTick", "(L" + UPGRADE_TILE + ";D)D", "getEnergyPerTick",
                new int[]{ALOAD, DLOAD}, DRETURN);
        replaceWithStaticCall(node, "getSecondaryEnergyPerTickMean", "(L" + UPGRADE_TILE + ";I)D", "getSecondaryEnergyPerTickMean",
                new int[]{ALOAD, ILOAD}, DRETURN);
        replaceWithStaticCall(node, "getMaxEnergy", "(L" + UPGRADE_TILE + ";D)D", "getMaxEnergy",
                new int[]{ALOAD, DLOAD}, DRETURN);
    }

    /**
     * Replace the whole body with "return Hooks.name(arguments)". The arguments are the parameters of the (static) method in order.
     */
    private void replaceWithStaticCall(ClassNode node, String name, String desc, String hook, int[] loads, int returnOpcode) {
        MethodNode method = findMethod(node, name, desc);
        if (method == null) throw new IllegalStateException("MekanismTweaks: method not found: " + node.name + "." + name + desc);

        InsnList list = new InsnList();
        int slot = 0;
        for (int load : loads) {
            list.add(new VarInsnNode(load, slot));
            slot += load == DLOAD ? 2 : 1;
        }
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, hook, desc, false));
        list.add(new InsnNode(returnOpcode));
        replaceBody(method, list);
    }

    // ---- Upgrade ----

    private void transformUpgrade(ClassNode node) {
        MethodNode getMax = findMethod(node, "getMax", "()I");
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, UPGRADE, "maxStack", "I"));
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "getMax", "(L" + UPGRADE + ";I)I", false));
        list.add(new InsnNode(IRETURN));
        replaceBody(getMax, list);

        MethodNode info = findMethod(node, "getMultScaledInfo", "(L" + UPGRADE_TILE + ";)Ljava/util/List;");
        list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "multScaledInfo", "(L" + UPGRADE + ";L" + UPGRADE_TILE + ";)Ljava/util/List;", false));
        list.add(new InsnNode(ARETURN));
        replaceBody(info, list);
    }

    // ---- StatUtils ----

    private void transformStatUtils(ClassNode node) {
        MethodNode method = findMethod(node, "inversePoisson", "(D)I");
        InsnList list = new InsnList();
        list.add(new VarInsnNode(DLOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "inversePoisson", "(D)I", false));
        list.add(new InsnNode(IRETURN));
        replaceBody(method, list);
    }

    // ---- ItemUpgrade ----

    /**
     * Limit MaxStackSize: the argument of setMaxStackSize goes through Hooks.stackSize.
     */
    private void transformItemUpgrade(ClassNode node) {
        int count = 0;
        for (MethodNode method : node.methods) {
            if (!method.name.equals("<init>")) continue;
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode call = (MethodInsnNode) insn;
                    if (call.getOpcode() == INVOKEVIRTUAL && call.desc.startsWith("(I)") && isSetMaxStackSize(call.name)) {
                        method.instructions.insertBefore(insn, new MethodInsnNode(INVOKESTATIC, HOOKS, "stackSize", "(I)I", false));
                        count++;
                    }
                }
            }
        }
        if (count == 0) throw new IllegalStateException("MekanismTweaks: setMaxStackSize not found in " + node.name);
    }

    /**
     * MCP name in a development environment, SRG name otherwise.
     */
    private boolean isSetMaxStackSize(String name) {
        return name.equals("func_77625_d") || name.equals("setMaxStackSize");
    }

    // ---- TileComponentUpgrade ----

    private void transformComponent(ClassNode node) {
        MethodNode tick = findMethod(node, "tick", "()V");
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "installAll", "(Lmekanism/common/tile/component/TileComponentUpgrade;)V", false));
        tick.instructions.insert(list);
    }

    // ---- GuiUpgradeTab ----

    /**
     * At the end of GuiUpgradeTab.renderForeground, call ClientHooks.renderWarning(tab, tab.tileEntity, x, y).
     */
    private void transformGui(ClassNode node) {
        int count = 0;
        for (MethodNode method : node.methods) {
            if (!method.name.equals("renderForeground") || !method.desc.equals("(II)V")) continue;
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn.getOpcode() == RETURN) {
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, node.name, "tileEntity", "Lnet/minecraft/tileentity/TileEntity;"));
                    list.add(new VarInsnNode(ILOAD, 1));
                    list.add(new VarInsnNode(ILOAD, 2));
                    list.add(new MethodInsnNode(INVOKESTATIC, CLIENT_HOOKS, "renderWarning",
                            "(Lmekanism/client/gui/element/GuiUpgradeTab;Lnet/minecraft/tileentity/TileEntity;II)V", false));
                    method.instructions.insertBefore(insn, list);
                    count++;
                }
            }
        }
        if (count == 0) throw new IllegalStateException("MekanismTweaks: renderForeground not found in " + node.name);
    }

    // ---- machines ----

    private void transformMachine(ClassNode node) {
        // the machine can be updated again to perform an excess operation
        node.interfaces.add(RERUN);
        MethodNode rerun = new MethodNode(ACC_PUBLIC, "mt$rerun", "()V", null, null);
        rerun.instructions.add(new VarInsnNode(ALOAD, 0));
        rerun.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, node.name, "onUpdate", "()V", false));
        rerun.instructions.add(new InsnNode(RETURN));
        node.methods.add(rerun);

        int updates = 0;
        for (MethodNode method : node.methods) {
            boolean concrete = (method.access & (ACC_ABSTRACT | ACC_NATIVE | ACC_BRIDGE | ACC_SYNTHETIC)) == 0;
            if (!concrete) continue;

            if (method.name.equals("operate") && method.desc.startsWith("(Lmekanism/common/recipe/machines/")) {
                method.instructions.insert(new MethodInsnNode(INVOKESTATIC, HOOKS, "operated", "()V", false));
            } else if (method.name.equals("onUpdate") && method.desc.equals("()V")) {
                updates++;
                for (AbstractInsnNode insn : method.instructions.toArray()) {
                    if (insn.getOpcode() == GETFIELD) {
                        FieldInsnNode field = (FieldInsnNode) insn;
                        if (field.desc.equals("D") && GUARDED_FIELDS.contains(field.name)) {
                            method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, HOOKS, "guard", "(D)D", false));
                        }
                    } else if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).name.equals("getDelay") && ((MethodInsnNode) insn).desc.equals("()I")) {
                        // the Digital Miner (it has no operate method): the delay is only set after it has mined a block
                        method.instructions.insertBefore(insn, new MethodInsnNode(INVOKESTATIC, HOOKS, "operated", "()V", false));
                    } else if (insn.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) insn).name.equals("suck")
                            && ((MethodInsnNode) insn).desc.equals("(Z)Z") && ((MethodInsnNode) insn).owner.equals(node.name)) {
                        // the Electric Pump: it has operated only if suck(true) has really pumped
                        method.instructions.set(insn, new MethodInsnNode(INVOKESTATIC, HOOKS, "suck",
                                "(Lmekanism/common/tile/TileEntityElectricPump;Z)Z", false));
                    } else if (insn.getOpcode() == RETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "afterUpdate", "(Ljava/lang/Object;)V", false));
                        method.instructions.insertBefore(insn, list);
                    }
                }
            }
        }
        if (updates == 0) throw new IllegalStateException("MekanismTweaks: onUpdate not found in " + node.name);
    }

    private void transformRecalculating(ClassNode node) {
        MethodNode method = findMethod(node, "recalculateUpgradables", "(L" + UPGRADE + ";)V");
        if (method == null) throw new IllegalStateException("MekanismTweaks: recalculateUpgradables not found in " + node.name);
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn.getOpcode() == RETURN) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "afterRecalc", "(Ljava/lang/Object;L" + UPGRADE + ";)V", false));
                method.instructions.insertBefore(insn, list);
            }
        }
    }

    private void transformProgress(ClassNode node) {
        MethodNode method = findMethod(node, "getScaledProgress", "()D");
        if (method == null) throw new IllegalStateException("MekanismTweaks: getScaledProgress not found in " + node.name);
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "scaledProgress", "(Ljava/lang/Object;)D", false));
        list.add(new InsnNode(DRETURN));
        replaceBody(method, list);
    }

    // ---- helpers ----

    private MethodNode findMethod(ClassNode node, String name, String desc) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) return method;
        }
        return null;
    }

    private void replaceBody(MethodNode method, InsnList body) {
        method.instructions.clear();
        method.tryCatchBlocks.clear();
        if (method.localVariables != null) method.localVariables.clear();
        method.instructions.add(body);
    }
}
