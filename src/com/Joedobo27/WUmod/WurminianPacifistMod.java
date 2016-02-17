package com.Joedobo27.WUmod;


import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
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
    }

    @Override
    public void onItemTemplatesCreated() {

        if (craftCottonBed) {
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
            } catch (IOException e) {}
        }
        if (craftCottonBed) {
            ItemTemplateBuilder cottonBed = new ItemTemplateBuilder("jdbCottonBed");
            setCottonBedID(IdFactory.getIdFor("jdbCottonBed", IdType.ITEMTEMPLATE));
            cottonBed.name("Bed", "Beds", "A cosy bed with a thick cotton comforter.");
            cottonBed.size(3);
            //cottonBed.descriptions();
            //new short[] { 109, 108, 21, 51, 52, 44, 86, 92, 31, 67, 135, 48, 110, 111, 176, 178, 157 }
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
            } catch (IOException e) {}
        }
    }

    @Override
    public void onServerStarted() {
        if (craftCottonBed) {
            AdvancedCreationEntry bed = CreationEntryCreator.createAdvancedEntry(10044, 482, 483, getCottonBedID(), false, false, 0.0f, true, true, CreationCategories.FURNITURE);
                bed.addRequirement(new CreationRequirement(1, 485, 1, true));
                bed.addRequirement(new CreationRequirement(4, 486, 4, true));
                bed.addRequirement(new CreationRequirement(10, 144, 10, true));
            logger.log(Level.INFO, "Cotton bed created and away to craft it added.");
        }
        if (craftCottonPelt) {
            CreationEntry towel = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.clothYard,
                    ItemList.clothString,getTowelItemID(),true,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            towel.setDepleteFromSource(squareGramsInTowel);
            towel.setDepleteFromTarget(stringGramsInTowel);
            CreationEntry pelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.scissors,
                    getTowelItemID(), ItemList.pelt, false, true, 0.0f, false, false, CreationCategories.TOOLS);
            pelt.setDepleteFromTarget(towelGrams);
            logger.log(Level.INFO, "Cotton Pelt created and away to craft it added.");
        }
        if (toolInWSToBS){
            try {
                ArrayList<Integer> created = new ArrayList<>(Arrays.asList(ItemList.knifeButchering, ItemList.knifeBladeButchering,
                        ItemList.knifeCarving, ItemList.knifeBladeCarving, ItemList.sickle, ItemList.sickleBlade,
                        ItemList.scythe, ItemList.scytheBlade));
                ArrayList<Integer> targets = new ArrayList<>(Arrays.asList(ItemList.knifeBladeButchering, ItemList.knifeBladeCarving, ItemList.scytheBlade,
                        ItemList.sickleBlade,ItemList.ironBar, ItemList.steelBar, ItemList.adamantineBar, ItemList.glimmerSteelBar, ItemList.seryllBar));

                Map<Integer, List<CreationEntry>> simpleEntries = ReflectionUtil.getPrivateField(CreationMatrix.class,
                        ReflectionUtil.getField(CreationMatrix.class, "simpleEntries"));
                Map<Integer, List<CreationEntry>> matrix = ReflectionUtil.getPrivateField(CreationMatrix.class,
                        ReflectionUtil.getField(CreationMatrix.class, "matrix"));

                //remove all simpleEntries matching the created items.
                HashMap<Integer, List<CreationEntry>> toDeleteSimple = new HashMap<>();
                for (HashMap.Entry<Integer, List<CreationEntry>> simpleEntries1 : simpleEntries.entrySet()) {
                    if (created.contains(simpleEntries1.getKey())){
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
            }catch (NoSuchFieldException | IllegalAccessException e){}

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
}
