package com.Joedobo27.common;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Forage;
import com.wurmonline.server.behaviours.ModifiedBy;
import javassist.*;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class ForageHerbData {
    private String fieldName;
    private int fieldNumber;
    private byte tileType;
    private GrassData.GrowthStage growthStage;
    private short action; // see behaviours.Actions. In Forage and Herb enum this is called "category".
    private int item; // see item.ItemList. Aka, itemType.
    private byte material; // if zero, then ItemFactory.createItem() will look up the template.getMaterial().
    // Further, I'm not sure why this here since every entry for forage and herb have a material of type 0.
    private int chanceAt1Skill;
    private int chanceAt100Skill;
    private int difficultyAt1Skill;
    private int difficultyAt100Skill;
    private ModifiedBy modifiedByCategory;
    private int modifierValue;


    public ForageHerbData(String _fieldName, int _fieldNumber, byte _TileType, GrassData.GrowthStage _growthStage,
                   short _action, int _item, byte _material, int _chanceAt1Skill, int _chanceAt100Skill, int _difficultyAt1Skill,
                   int _difficultyAt100Skill, ModifiedBy _modifiedByCategory, int _modifierValue){

        fieldName = _fieldName;
        fieldNumber = _fieldNumber;
        tileType = _TileType;
        growthStage = _growthStage;
        action = _action;
        item = _item;
        material = _material;
        chanceAt1Skill = _chanceAt1Skill;
        chanceAt100Skill = _chanceAt100Skill;
        difficultyAt1Skill = _difficultyAt1Skill;
        difficultyAt100Skill = _difficultyAt100Skill;
        modifiedByCategory = _modifiedByCategory;
        modifierValue = _modifierValue;
    }

    public static void extendForageEnumReflection(ForageHerbData obj) throws ClassNotFoundException, NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Class forageClass = Class.forName("com.wurmonline.server.behaviours.Forage");
        @SuppressWarnings("unchecked") Constructor constructor = forageClass.getConstructor(String.class, Integer.class, Boolean.class,
                Class.forName("com.wurmonline.mesh.GrassData$GrowthStage"), Short.class, Integer.class, Boolean.class, Integer.class, Integer.class,
                Integer.class, Integer.class, Class.forName("com.wurmonline.server.behaviours.ModifiedBy"), Integer.class);
        Forage forage = (Forage) constructor.newInstance(obj.fieldName, obj.fieldNumber, obj.tileType, obj.growthStage, obj.action,
                obj.item, obj.material, obj.chanceAt1Skill, obj.chanceAt100Skill, obj.difficultyAt1Skill, obj.difficultyAt100Skill,
                obj.modifiedByCategory, obj.modifierValue);

        Field field = forageClass.getField(obj.fieldName);
        ReflectionUtil.setPrivateField(forageClass, field, forage);

    }

    public static void extendForageEnumBytecode(ClassPool pool, ForageHerbData obj) throws ClassNotFoundException, NotFoundException, CannotCompileException {
        //JAssistClassData forageClass = new JAssistClassData("com.wurmonline.server.behaviours.Forage", pool);
        //JAssistMethodData field_ini = new JAssistMethodData(forageClass,

        // "// com/wurmonline/server/behaviours/Forage.\"<init>\":(Ljava/lang/String;IBLcom/wurmonline/mesh/GrassData$GrowthStage;SIBIIIILcom/wurmonline/server/behaviours/ModifiedBy;I)V", "<init>");
        Class forageClass = Class.forName("com.wurmonline.server.behaviours.Forage");
        CtClass forage = pool.get("com.wurmonline.server.behaviours.Forage");

        CtField field = new CtField(forage, obj.fieldName, forage);
        field.setModifiers(Modifier.PUBLIC & Modifier.STATIC & Modifier.FINAL & Modifier.ENUM);
        forage.addField(field);
    }
}
