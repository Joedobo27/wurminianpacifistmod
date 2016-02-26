package com.Joedobo27.WUmod;


import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WurminianPacifistMod implements WurmMod, Initable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

    private boolean craftCottonPelt = false;
    private int towelX = 5;
    private int towelY = 10;
    private int towelZ = 10;
    private int towelGrams = 2000;
    private float towelDifficulty = 20.0f;
    private int towelValue = 30000;
    private boolean toolInWSToBS = false;
    private int squareGramsInTowel = 1200;
    private int stringGramsInTowel = 800;
    private int towelItemID;
    private boolean craftCottonBed = false;
    private int cottonBedID;
    private boolean redDyeFromMadder = false;
    private int madderID;
    private boolean cheeseDrillWithCloth = false;
    private boolean nettleTeaAsRennet = false;
    private int nettleTeaID;
    private int cheeseDrillID;
    private boolean craftGourdCanteen = false;
    private boolean craftCottonToolBelt = false;
    private int cottonToolbeltID;

    private CodeAttribute getCompositeColorAttribute;
    private CodeIterator getCompositeColorIterator;
    private MethodInfo getCompositeColorMInfo;

    private Logger logger = Logger.getLogger(WurminianPacifistMod.class.getName());

    @Override
    public void configure(Properties properties) {
        craftCottonPelt = Boolean.valueOf(properties.getProperty("craftCottonPelt", Boolean.toString(craftCottonPelt)));
        towelX = Integer.valueOf(properties.getProperty("towelX", Integer.toString(towelX)));
        towelY = Integer.valueOf(properties.getProperty("towelY", Integer.toString(towelY)));
        towelZ = Integer.valueOf(properties.getProperty("towelZ", Integer.toString(towelZ)));
        towelGrams = Integer.valueOf(properties.getProperty("towelGrams", Integer.toString(towelGrams)));
        towelDifficulty = Float.valueOf(properties.getProperty("towelDifficulty", Float.toString(towelDifficulty)));
        towelValue = Integer.valueOf(properties.getProperty("towelValue", Integer.toString(towelValue)));
        squareGramsInTowel = Integer.valueOf(properties.getProperty("squareGramsInTowel", Integer.toString(squareGramsInTowel)));
        stringGramsInTowel = Integer.valueOf(properties.getProperty("stringGramsInTowel", Integer.toString(stringGramsInTowel)));
        toolInWSToBS = Boolean.valueOf(properties.getProperty("toolInWSToBS", Boolean.toString(toolInWSToBS)));
        craftCottonBed = Boolean.valueOf(properties.getProperty("craftCottonBed", Boolean.toString(craftCottonBed)));
        redDyeFromMadder = Boolean.valueOf(properties.getProperty("redDyeFromMadder", Boolean.toString(redDyeFromMadder)));
        cheeseDrillWithCloth = Boolean.valueOf(properties.getProperty("cheeseDrillWithCloth", Boolean.toString(cheeseDrillWithCloth)));
        nettleTeaAsRennet = Boolean.valueOf(properties.getProperty("nettleTeaAsRennet", Boolean.toString(nettleTeaAsRennet)));
        craftGourdCanteen = Boolean.valueOf(properties.getProperty("craftGourdCanteen", Boolean.toString(craftGourdCanteen)));
        craftCottonToolBelt = Boolean.valueOf(properties.getProperty("craftCottonToolBelt", Boolean.toString(craftCottonToolBelt)));
    }

    @Override
    public void onItemTemplatesCreated() {

        if (craftCottonPelt) {
            ItemTemplateBuilder towel = new ItemTemplateBuilder("jdbTowel");
            setTowelItemID(IdFactory.getIdFor("jdbTowel", IdType.ITEMTEMPLATE));
            towel.name("towel", "towels", "A thick piece of cloth with many looped strings protruding from the surface.");
            towel.size(3);
            //towel.descriptions();
            towel.itemTypes(new short[]{ItemTypes.ITEM_TYPE_CLOTH, ItemTypes.ITEM_TYPE_BULK});
            towel.imageNumber((short) 640);
            towel.behaviourType((short) 1);
            towel.combatDamage(0);
            towel.decayTime(3024000L);
            towel.dimensions(towelX, towelY, towelZ);
            towel.primarySkill(-10);
            //towel.bodySpaces();
            towel.modelName("model.resource.yard.");
            towel.difficulty(towelDifficulty);
            towel.weightGrams(towelGrams);
            towel.material((byte) 17);
            towel.value(towelValue);
            towel.isTraded(true);
            //towel.armourType();
            try {
                towel.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (craftCottonBed) {
            ItemTemplateBuilder cottonBed = new ItemTemplateBuilder("jdbCottonBed");
            setCottonBedID(IdFactory.getIdFor("jdbCottonBed", IdType.ITEMTEMPLATE));
            cottonBed.name("Bed", "Beds", "A cosy bed with a thick cotton comforter.");
            cottonBed.size(3);
            //cottonBed.descriptions();
            cottonBed.itemTypes(new short[]{109, 108, 21, 51, 52, 44, 86, 92, 31, 67, 135, 48, 110, 111, 176, 178, 157});
            cottonBed.imageNumber((short) 313);
            cottonBed.behaviourType((short) 1);
            cottonBed.combatDamage(0);
            cottonBed.decayTime(9072000L);
            cottonBed.dimensions(60, 60, 200);
            cottonBed.primarySkill(-10);
            //cottonBed.bodySpaces();
            cottonBed.modelName("model.furniture.bed.standard.");
            cottonBed.difficulty(20.0f);
            cottonBed.weightGrams(40000);
            cottonBed.material((byte) 14);
            cottonBed.value(10000);
            cottonBed.isTraded(true);
            //cottonBed.armourType();
            try {
                cottonBed.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (redDyeFromMadder) {
            // for now, using lovage's graphic and woad's details.
            ItemTemplateBuilder madder = new ItemTemplateBuilder("jdbMadder");
            setMadderID(IdFactory.getIdFor("jdbMadder", IdType.ITEMTEMPLATE));
            madder.name("Madder", "Madders", "A plant with vibrant red roots.");
            madder.size(3);
            //cottonBed.descriptions();
            madder.itemTypes(new short[]{46, 146, 164});
            madder.imageNumber((short) 711);
            madder.behaviourType((short) 1);
            madder.combatDamage(0);
            madder.decayTime(9072000L);
            madder.dimensions(3, 3, 3);
            madder.primarySkill(-10);
            //cottonBed.bodySpaces();
            madder.modelName("model.herb.lovage.");
            madder.difficulty(100.0f);
            madder.weightGrams(50);
            madder.material((byte) 22);
            madder.value(100);
            madder.isTraded(true);
            //cottonBed.armourType();
            try {
                madder.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cheeseDrillWithCloth) {
            // 1kg nettle + 1L watter is 1l Nettle tea (which is also a rennet)
            // 0.5L Nettle tea + 2L milk = 0.5 kg cheese. Use cheese's standard item stats.
            ItemTemplateBuilder cheeseDrill = new ItemTemplateBuilder("jdbCheeseDrill");
            setCheeseDrillID(IdFactory.getIdFor("jdbCheeseDrill", IdType.ITEMTEMPLATE));
            cheeseDrill.name("cheese drill", "cheese drills", "A wooden press used to compress cheese curds and separate out whey.");
            cheeseDrill.size(3);
            //cottonBed.descriptions();
            cheeseDrill.itemTypes(new short[]{108, 44, 144, 38, 21, 92, 147, 51});
            cheeseDrill.imageNumber((short) 266);
            cheeseDrill.behaviourType((short) 1);
            cheeseDrill.combatDamage(0);
            cheeseDrill.decayTime(9072000L);
            cheeseDrill.dimensions(15, 15, 50);
            cheeseDrill.primarySkill(-10);
            //cottonBed.bodySpaces();
            cheeseDrill.modelName("model.tool.cheesedrill.");
            cheeseDrill.difficulty(30.0f);
            cheeseDrill.weightGrams(3000);
            cheeseDrill.material((byte) 14);
            cheeseDrill.value(10000);
            cheeseDrill.isTraded(true);
            //cottonBed.armourType();
            try {
                cheeseDrill.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (craftCottonToolBelt) {
            ItemTemplateBuilder cottonToolBelt = new ItemTemplateBuilder("jdbCottonToolBelt");
            setCottonToolbeltID(IdFactory.getIdFor("jdbCottonToolBelt", IdType.ITEMTEMPLATE));
            cottonToolBelt.name("toolbelt", "toolbelts", "An ingenious system of pockets, pouches, hooks and holes designed to keep a wide array of common tools.");
            cottonToolBelt.size(3);
            //cottonBed.descriptions();
            cottonToolBelt.itemTypes(new short[] { 108, 44, 24, 92, 147, 121, 97 });
            cottonToolBelt.imageNumber((short) 861);
            cottonToolBelt.behaviourType((short) 1);
            cottonToolBelt.combatDamage(0);
            cottonToolBelt.decayTime(3024000L);
            cottonToolBelt.dimensions(2, 5, 10);
            cottonToolBelt.primarySkill(-10);
            cottonToolBelt.bodySpaces(new byte[] { 34, 43 });
            cottonToolBelt.modelName("model.clothing.belt.toolbelt.");
            cottonToolBelt.difficulty(35.0f);
            cottonToolBelt.weightGrams(1000);
            cottonToolBelt.material((byte) 17);
            cottonToolBelt.value(10000);
            cottonToolBelt.isTraded(true);
            //cottonBed.armourType();
            try {
                cottonToolBelt.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServerStarted() {
        if (craftCottonBed) {
            AdvancedCreationEntry bed = CreationEntryCreator.createAdvancedEntry(10044, 482, 483, getCottonBedID(), false, false, 0.0f, true, true, CreationCategories.FURNITURE);
                bed.addRequirement(new CreationRequirement(1, 485, 1, true));
                bed.addRequirement(new CreationRequirement(4, 486, 4, true));
                bed.addRequirement(new CreationRequirement(10, 144, 10, true));
            logger.log(Level.INFO, "Cotton bed created and a away to craft it added.");
        }
        if (craftCottonPelt) {
            CreationEntry towel = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.clothYard,
                    ItemList.clothString,getTowelItemID(),true,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            towel.setDepleteFromSource(squareGramsInTowel);
            towel.setDepleteFromTarget(stringGramsInTowel);
            CreationEntry pelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.scissors,
                    getTowelItemID(), ItemList.pelt, false, true, 0.0f, false, false, CreationCategories.TOOLS);
            pelt.setDepleteFromTarget(towelGrams);
            logger.log(Level.INFO, "Cotton Pelt created and a away to craft it added.");
        }
        if (redDyeFromMadder) {
            CreationEntry redMadder = CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.water,
                    getMadderID(), ItemList.dyeRed, true, true, 0.0f, false, false, CreationCategories.DYES);

            //Adding to Forage enum class.
            Class<?> clz = WurminianPacifistMod.class; // Default value of self.
            try {
                clz = Class.forName("com.wurmonline.server.behaviours.Forage");
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            Object[] constants = clz.getEnumConstants();
            logger.log(Level.INFO, Arrays.toString(constants));
        }
        if (cheeseDrillWithCloth) {
            AdvancedCreationEntry clothCheeseDrill = CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.plank, ItemList.shaft,
                    getCheeseDrillID(), false, false, 0.0f, true, false, CreationCategories.TOOLS);
            clothCheeseDrill.addRequirement(new CreationRequirement(1, ItemList.plank, 4, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(2, ItemList.sprout, 2, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(3, ItemList.nailsIronSmall, 1, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(4, ItemList.clothYard, 2, true));
            logger.log(Level.INFO, "CheeseDrill from cloth squares added and a way to craft it");
        }
        if (craftCottonToolBelt) {
            CreationEntry cottonToolBelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.metalHooks, ItemList.clothYard,
                    getCottonToolbeltID(), true, true, 0.0f, false, false, CreationCategories.CLOTHES);
            cottonToolBelt.setDepleteFromTarget(1500);
        }
        if (toolInWSToBS){

            ArrayList<Integer> created = new ArrayList<>(Arrays.asList(ItemList.knifeButchering, ItemList.knifeBladeButchering,
                    ItemList.knifeCarving, ItemList.knifeBladeCarving, ItemList.sickle, ItemList.sickleBlade,
                    ItemList.scythe, ItemList.scytheBlade));
            ArrayList<Integer> targets = new ArrayList<>(Arrays.asList(ItemList.knifeBladeButchering, ItemList.knifeBladeCarving, ItemList.scytheBlade,
                    ItemList.sickleBlade, ItemList.ironBar, ItemList.steelBar, ItemList.adamantineBar, ItemList.glimmerSteelBar, ItemList.seryllBar));

            Map<Integer, List<CreationEntry>> simpleEntries = new HashMap<>();
            try {
                simpleEntries = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "simpleEntries"));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            Map<Integer, List<CreationEntry>> matrix = new HashMap<>();
            try {
                matrix = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "matrix"));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            //remove all simpleEntries matching the created items.
            HashMap<Integer, List<CreationEntry>> toDeleteSimple = new HashMap<>();
            for (HashMap.Entry<Integer, List<CreationEntry>> simpleEntries1 : simpleEntries.entrySet()) {
                if (created.contains(simpleEntries1.getKey())) {
                    toDeleteSimple.put(simpleEntries1.getKey(), simpleEntries1.getValue());
                }
            }
            for (HashMap.Entry<Integer, List<CreationEntry>> delete : toDeleteSimple.entrySet()) {
                simpleEntries.remove(delete.getKey(), delete.getValue());
            }

            // Remove all matrix entries for the combination of targets and created. Only those that match can be removed.
            // For example, many items have ironBar as as a target but we must only remove those that also match created.
            HashMap<Integer, List<CreationEntry>> toDeleteMatrix = new HashMap<>();
            List<CreationEntry> entries;
            for (HashMap.Entry<Integer, List<CreationEntry>> matrix1 : matrix.entrySet()) {
                if (targets.contains(matrix1.getKey())) {
                    if (!toDeleteMatrix.containsKey(matrix1.getKey())) {
                        entries = new ArrayList<>();
                        toDeleteMatrix.put(matrix1.getKey(), entries);
                    }
                    for (CreationEntry entry : matrix1.getValue()) {
                        if (created.contains(entry.getObjectCreated())) {
                            toDeleteMatrix.get(matrix1.getKey()).add(entry);
                        }
                    }
                }
            }
            for (HashMap.Entry<Integer, List<CreationEntry>> delete : toDeleteMatrix.entrySet()) {
                matrix.get(delete.getKey());
                for (CreationEntry entry : delete.getValue()) {
                    if (matrix.get(delete.getKey()).contains(entry)) {
                        matrix.get(delete.getKey()).remove(entry);
                    }
                }
            }


            /*
            knifeButchering createSimpleEntry(1016, 99, 125, 93, true, true, 0.0f, false, false, CreationCategories.TOOLS);
            createSimpleEntry(10010, 64, 46, 125, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
            carver createSimpleEntry(1016, 99, 126, 8, true, true, 0.0f, false, false, CreationCategories.TOOLS);
            carver blade createSimpleEntry(10010, 64, 46, 126, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
            scythe createSimpleEntry(1016, 23, 270, 268, true, true, 0.0f, false, false, CreationCategories.TOOLS);
            scythe blade createSimpleEntry(10010, 185, 46, 270, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
            sickle createSimpleEntry(1016, 99, 269, 267, true, true, 0.0f, false, false, CreationCategories.TOOLS);
            sickle blade  createSimpleEntry(10010, 185, 46, 269, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
            */

            CreationEntry knifeButchering = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.knifeBladeButchering,ItemList.knifeButchering,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry knifeBladeButcheringIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.ironBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.steelBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.adamantineBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.glimmerSteelBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.seryllBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            
            CreationEntry KnifeCarving = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.knifeBladeCarving,ItemList.knifeCarving,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry knifeBladeCarvingIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.ironBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.steelBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.adamantineBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.glimmerSteelBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.seryllBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            
            CreationEntry scythe = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.shaft,
                    ItemList.scytheBlade,ItemList.scythe,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry scytheBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.ironBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scytheBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.steelBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scythebladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.adamantineBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scythebladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.glimmerSteelBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scytheBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.seryllBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            
            CreationEntry sickle = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.sickleBlade,ItemList.sickle,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry sickleBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.ironBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.steelBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.adamantineBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.glimmerSteelBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.seryllBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);

            logger.log(Level.INFO, "Tools crafted with WS switch over to BS");
        }

    }

    @Override
    public void init() {
        String replaceByteResult;
        String printByteResult;
        jaseBT jbt;
        jaseBT jbt1;


        ClassPool pool = HookManager.getInstance().getClassPool();

        CtClass ctcWurmColor = pool.makeClass("Default");
        try {
            ctcWurmColor = pool.get("com.wurmonline.server.items.WurmColor");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        ClassFile cfWurmColor = ctcWurmColor.getClassFile();
        ConstPool cpWurmColor = cfWurmColor.getConstPool();

        if (redDyeFromMadder) {
            setGetCompositeColor(cfWurmColor,"(IIIF)I","GetCompositeColor");
            try {
                getGetCompositeColorIterator().insertGap(9, 7);
            } catch (BadBytecode badBytecode) {
                badBytecode.printStackTrace();
            }
            jbt = new jaseBT();
            //<editor-fold desc="Change information.">
            // insert 7 wide gap at line 9.
            // this--if (itemTemplateId == 439) {
            // becomes-- if (itemTemplateId == 439 || itemTemplateID == ??) {
            // Where ?? is a value picked for Madder and inserted with with ConstantPool.addIntegerInfo()
            //</editor-fold>
            jbt.setOpCodeStructure(new ArrayList<>(Arrays.asList(Opcode.IF_ICMPEQ, Opcode.ILOAD_2, Opcode.LDC_W)));
            jbt.setOperandStructure(new ArrayList<>(Arrays.asList("000f","",
                    String.format("%04X", cpWurmColor.addIntegerInfo(getMadderID())))));
            jbt.setOpcodeOperand();
            replaceByteResult = jaseBT.byteCodeFindReplace("00,00,00,00,00,00,00","00,00,00,00,00,00,00",jbt.getOpcodeOperand(),getGetCompositeColorIterator(),
                    "GetCompositeColor");
            try {
                getGetCompositeColorMInfo().rebuildStackMapIf6(pool,cfWurmColor);
            } catch (BadBytecode badBytecode) {
                badBytecode.printStackTrace();
            }
            logger.log(Level.INFO, replaceByteResult);
        }
    }

    public void setTowelItemID(int towelItemID) {
        this.towelItemID = towelItemID;
    }

    public int getTowelItemID() {
        return towelItemID;
    }

    public void setCottonBedID(int cottonBedID) {
        this.cottonBedID = cottonBedID;
    }

    public int getCottonBedID() {
        return cottonBedID;
    }

    public void setMadderID(int madderID) {
        this.madderID = madderID;
    }

    public int getMadderID() {
        return madderID;
    }

    public void setCheeseDrillID(int cheeseDrillID) {
        this.cheeseDrillID = cheeseDrillID;
    }

    public int getCheeseDrillID() {
        return cheeseDrillID;
    }

    public void setCottonToolbeltID(int cottonToolbeltID) {
        this.cottonToolbeltID = cottonToolbeltID;
    }

    public int getCottonToolbeltID() {
        return cottonToolbeltID;
    }

    public void setGetCompositeColor(ClassFile cf, String desc, String name){
        if (this.getCompositeColorMInfo == null || this.getCompositeColorIterator == null || this.getCompositeColorAttribute == null){
            for (List a : new List[]{cf.getMethods()}){
                for (Object b : a){
                    MethodInfo MInfo = (MethodInfo) b;
                    if (Objects.equals(MInfo.getDescriptor(), desc) && Objects.equals(MInfo.getName(), name)){
                        this.getCompositeColorMInfo = MInfo;
                        break;
                    }
                }
            }
            if (this.getCompositeColorMInfo == null){
                throw new NullPointerException();
            }
            this.getCompositeColorAttribute = this.getCompositeColorMInfo.getCodeAttribute();
            this.getCompositeColorIterator = this.getCompositeColorAttribute.iterator();
        }
    }

    public CodeIterator getGetCompositeColorIterator() {
        return getCompositeColorIterator;
    }

    public CodeAttribute getGetCompositeColorAttribute() {
        return getCompositeColorAttribute;
    }

    public MethodInfo getGetCompositeColorMInfo() {
        return getCompositeColorMInfo;
    }
}
