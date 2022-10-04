package dev.felnull.mekanismtweaks;

// Variables for Mixin class
public class Temp {
    public static String name;//remembering upgrade name
    public static boolean isInjectingToCachedRecipe = false;//not to call injected method while operating injected method
    public static boolean isInjectingToFormulaicAssemblicator = false;//not to call injected method while operating injected method
    public static boolean isInjectingToFluidicPlenisher = false;//not to call injected method while operating injected method
    public static boolean isInjectingToPump = false;//not to call injected method while operating injected method
    public static boolean extractFlag = false;//to identify execute extract
}
