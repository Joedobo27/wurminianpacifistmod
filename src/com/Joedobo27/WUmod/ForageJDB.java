package com.Joedobo27.WUmod;


import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.ModifiedBy;
import com.wurmonline.server.creatures.Creature;

import java.util.*;


@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ForageJDB
{
    private final String forageDescriptor;
    private final int forageOrdinal;
    private final byte tileType;
    private final GrassData.GrowthStage grassLength;
    private final short category;
    private final int itemType;
    private final byte material;
    private final int chanceAt1;
    private final int chanceAt100;
    private final int difficultyAt1;
    private final int difficultyAt100;
    private final ModifiedBy modifiedBy;
    private final int chanceModifier;
    public static ArrayList<Object[]> forageData;
    private static ForageJDB FORAGE_DEFAULT = new ForageJDB("",0,(byte)0,GrassData.GrowthStage.SHORT,(short)0, 0, (byte)0, 0, 0, 0, 0, ModifiedBy.NOTHING, 0);

    private ForageJDB(final String aForageDescriptor, final int aForageOrdinal, final byte aTileType, final GrassData.GrowthStage aGrassLength, final short aCategory, final int aItemType, final byte aMaterial, final int aChanceAt1, final int aChanceAt100, final int aDifficultyAt1, final int aDifficultyAt100, final ModifiedBy aModifiedBy, final int aChanceModifier) {
        this.forageDescriptor = aForageDescriptor;
        this.forageOrdinal = aForageOrdinal;
        this.tileType = aTileType;
        this.grassLength = aGrassLength;
        this.category = aCategory;
        this.itemType = aItemType;
        this.material = aMaterial;
        this.chanceAt1 = aChanceAt1;
        this.chanceAt100 = aChanceAt100;
        this.difficultyAt1 = aDifficultyAt1;
        this.difficultyAt100 = aDifficultyAt100;
        this.modifiedBy = aModifiedBy;
        this.chanceModifier = aChanceModifier;
    }

    private float getChanceAt(final Creature performer, final int knowledge, final int tilex, final int tiley) {
        final float chance = this.chanceAt1 + (this.chanceAt100 - this.chanceAt1) / 100 * knowledge;
        return chance + this.modifiedBy.chanceModifier(performer, this.chanceModifier, tilex, tiley);
    }

    public static ForageJDB getRandomForage(Creature performer, byte aTileType, GrassData.GrowthStage aGrassLength, short aCategory, int knowledge, int tileX, int tileY){
        ForageJDB f = null;
        Tiles.Tile theTile = Tiles.getTile(aTileType);
        if (theTile.isNormalTree()) {
            aTileType = Tiles.Tile.TILE_TREE.id;
        }
        if (theTile.isNormalBush()) {
            aTileType = Tiles.Tile.TILE_BUSH.id;
        }
        for (Object[] entry : forageData) {
            // if (forageData.tileType == aTileType && forageData.grassLength == aGrassLength){
            if ((byte) entry[2] == aTileType && (aTileType != Tiles.Tile.TILE_GRASS.id || entry[3] == aGrassLength)) {
                f = new ForageJDB((String) entry[0], (int) entry[1], (byte) entry[2],(GrassData.GrowthStage) entry[3], (short) entry[4],
                        (int) entry[5], (byte) entry[6], (int) entry[7], (int) entry[8], (int) entry[9], (int) entry[10], (ModifiedBy)entry[11],
                        (int) entry[12]);
                break;
            }
        }
        float chance = f.getChanceAt(performer, knowledge, tileX, tileY);
        if (chance == 0.0f) {
            return null;
        }
        int randomChance = Server.rand.nextInt((int)chance);
        if (randomChance < chance && (aCategory == 223 || aCategory == f.category)){
            return f;
        }
        return null;
    }

    public static void setForageData() {
        ForageJDB.forageData = new ArrayList<>(Arrays.asList(
                //<editor-fold desc="Forage data setup">
                new Object[]{"GSHORT_BLUEBERRY", 0, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_CORN", 1, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_COTTON", 2, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 15},
                new Object[]{"GSHORT_HAZELNUTS", 3, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GSHORT_LINGONBERRY", 4, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"GSHORT_MUSHROOM_BLACK", 5, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_MUSHROOM_BLUE", 6, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_MUSHROOM_BROWN", 7, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_MUSHROOM_GREEN", 8, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_MUSHROOM_RED", 9, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_MUSHROOM_YELLOW", 10, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 4},
                new Object[]{"GSHORT_ONION", 11, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_POTATO", 12, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_PUMPKIN", 13, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_STRAWBERRY", 14, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GSHORT_WEMP_PLANT", 15, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"GSHORT_EASTER_EGG", 16, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 466, (byte) 0, 0, 0, -5, -5, ModifiedBy.EASTER, 20},
                new Object[]{"GSHORT_RICE", 17, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 40},
                new Object[]{"GSHORT_IVY", 18, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 917, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_GRAPE", 19, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 918, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_ROSE", 20, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 1017, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GSHORT_ROCK", 21, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, 684, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_BLUEBERRY", 22, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_CORN", 23, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_COTTON", 24, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"GMED_HAZELNUTS", 25, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GMED_LINGONBERRY", 26, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"GMED_MUSHROOM_BLACK", 27, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_MUSHROOM_BLUE", 28, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_MUSHROOM_BROWN", 29, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_MUSHROOM_GREEN", 30, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_MUSHROOM_RED", 31, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_MUSHROOM_YELLOW", 32, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 8},
                new Object[]{"GMED_ONION", 33, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_POTATO", 34, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_PUMPKIN", 35, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_STRAWBERRY", 36, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GMED_WEMP_PLANT", 37, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"GMED_EASTER_EGG", 38, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 571, 466, (byte) 0, 0, 0, -5, -5, ModifiedBy.EASTER, 20},
                new Object[]{"GMED_RICE", 39, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 30},
                new Object[]{"GMED_IVY", 40, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 917, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_GRAPE", 41, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 918, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_ROSE", 42, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 1017, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GMED_ROCK", 43, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, 684, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_BLUEBERRY", 44, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_CORN", 45, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_COTTON", 46, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"GTALL_HAZELNUTS", 47, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GTALL_LINGONBERRY", 48, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"GTALL_MUSHROOM_BLACK", 49, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_MUSHROOM_BLUE", 50, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_MUSHROOM_BROWN", 51, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_MUSHROOM_GREEN", 52, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_MUSHROOM_RED", 53, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_MUSHROOM_YELLOW", 54, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GTALL_ONION", 55, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_POTATO", 56, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_PUMPKIN", 57, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_STRAWBERRY", 58, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GTALL_WEMP_PLANT", 59, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"GTALL_EASTER_EGG", 60, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 571, 466, (byte) 0, 0, 0, -5, -5, ModifiedBy.EASTER, 20},
                new Object[]{"GTALL_RICE", 61, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"GTALL_IVY", 62, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 917, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_GRAPE", 63, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 918, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_ROSE", 64, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 1017, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GTALL_ROCK", 65, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, 684, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_BLUEBERRY", 66, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_CORN", 67, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_COTTON", 68, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"GWILD_HAZELNUTS", 69, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GWILD_LINGONBERRY", 70, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"GWILD_MUSHROOM_BLACK", 71, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_MUSHROOM_BLUE", 72, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_MUSHROOM_BROWN", 73, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_MUSHROOM_GREEN", 74, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_MUSHROOM_RED", 75, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_MUSHROOM_YELLOW", 76, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"GWILD_ONION", 77, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_POTATO", 78, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_PUMPKIN", 79, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_STRAWBERRY", 80, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"GWILD_WEMP_PLANT", 81, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"GWILD_EASTER_EGG", 82, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 571, 466, (byte) 0, 0, 0, -5, -5, ModifiedBy.EASTER, 20},
                new Object[]{"GWILD_RICE", 83, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 10},
                new Object[]{"GWILD_IVY", 84, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 917, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_GRAPE", 85, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 918, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_ROSE", 86, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 1017, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"GWILD_ROCK", 87, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, 684, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_BLUEBERRY", 88, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_CORN", 89, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_COTTON", 90, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"STEPPE_HAZELNUTS", 91, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"STEPPE_LINGONBERRY", 92, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"STEPPE_MUSHROOM_BLACK", 93, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_MUSHROOM_BLUE", 94, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_MUSHROOM_BROWN", 95, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_MUSHROOM_GREEN", 96, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_MUSHROOM_RED", 97, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_MUSHROOM_YELLOW", 98, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"STEPPE_ONION", 99, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_POTATO", 100, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_PUMPKIN", 101, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"STEPPE_STRAWBERRY", 102, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"STEPPE_WEMP_PLANT", 103, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"STEPPE_RICE", 104, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"TUNDRA_BLUEBERRY", 105, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 25, 25, 10, 10, ModifiedBy.NO_TREES, 10},
                new Object[]{"TUNDRA_COTTON", 106, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"TUNDRA_HAZELNUTS", 107, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"TUNDRA_LINGONBERRY", 108, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 20, 20, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"TUNDRA_MUSHROOM_BLACK", 109, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_MUSHROOM_BLUE", 110, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_MUSHROOM_BROWN", 111, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_MUSHROOM_GREEN", 112, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_MUSHROOM_RED", 113, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_MUSHROOM_YELLOW", 114, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TUNDRA_STRAWBERRY", 115, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 15, 15, -5, -5, ModifiedBy.HUNGER, 10},
                new Object[]{"TUNDRA_WEMP_PLANT", 116, Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 5},
                new Object[]{"MARSH_BLUEBERRY", 117, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"MARSH_CORN", 118, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 32, (byte) 0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"MARSH_COTTON", 119, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"MARSH_HAZELNUTS", 120, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"MARSH_LINGONBERRY", 121, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"MARSH_MUSHROOM_BLACK", 122, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_MUSHROOM_BLUE", 123, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_MUSHROOM_BROWN", 124, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_MUSHROOM_GREEN", 125, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_MUSHROOM_RED", 126, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_MUSHROOM_YELLOW", 127, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"MARSH_ONION", 128, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"MARSH_POTATO", 129, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"MARSH_PUMPKIN", 130, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"MARSH_STRAWBERRY", 131, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"MARSH_WEMP_PLANT", 132, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10},
                new Object[]{"MARSH_RICE", 133, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 50},
                new Object[]{"TSHORT_BLUEBERRY", 134, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_BRANCH", 135, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, 688, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_CORN", 136, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_COTTON", 137, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"TSHORT_HAZELNUTS", 138, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"TSHORT_LINGONBERRY", 139, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"TSHORT_MUSHROOM_BLACK", 140, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_MUSHROOM_BLUE", 141, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_MUSHROOM_BROWN", 142, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_MUSHROOM_GREEN", 143, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_MUSHROOM_RED", 144, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_MUSHROOM_YELLOW", 145, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TSHORT_ONION", 146, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_POTATO", 147, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_PUMPKIN", 148, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TSHORT_STRAWBERRY", 149, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"TSHORT_WEMP_PLANT", 150, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"TSHORT_RICE", 151, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"TMED_BLUEBERRY", 152, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_BRANCH", 153, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, 688, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_CORN", 154, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_COTTON", 155, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"TMED_HAZELNUTS", 156, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"TMED_LINGONBERRY", 157, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"TMED_MUSHROOM_BLACK", 158, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_MUSHROOM_BLUE", 159, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_MUSHROOM_BROWN", 160, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_MUSHROOM_GREEN", 161, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_MUSHROOM_RED", 162, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_MUSHROOM_YELLOW", 163, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TMED_ONION", 164, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_POTATO", 165, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_PUMPKIN", 166, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TMED_STRAWBERRY", 167, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"TMED_WEMP_PLANT", 168, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"TMED_RICE", 169, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"TTALL_BLUEBERRY", 170, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_BRANCH", 171, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, 688, (byte) 0, 18, 18, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_CORN", 172, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_COTTON", 173, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"TTALL_HAZELNUTS", 174, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"TTALL_LINGONBERRY", 175, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"TTALL_MUSHROOM_BLACK", 176, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_MUSHROOM_BLUE", 177, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_MUSHROOM_BROWN", 178, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_MUSHROOM_GREEN", 179, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_MUSHROOM_RED", 180, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_MUSHROOM_YELLOW", 181, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"TTALL_ONION", 182, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_POTATO", 183, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_PUMPKIN", 184, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"TTALL_STRAWBERRY", 185, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"TTALL_WEMP_PLANT", 186, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"TTALL_RICE", 187, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"BSHORT_BLUEBERRY", 188, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"BSHORT_CORN", 189, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BSHORT_COTTON", 190, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"BSHORT_HAZELNUTS", 191, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"BSHORT_LINGONBERRY", 192, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"BSHORT_MUSHROOM_BLACK", 193, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_MUSHROOM_BLUE", 194, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_MUSHROOM_BROWN", 195, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_MUSHROOM_GREEN", 196, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_MUSHROOM_RED", 197, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_MUSHROOM_YELLOW", 198, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BSHORT_ONION", 199, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BSHORT_POTATO", 200, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BSHORT_PUMPKIN", 201, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BSHORT_STRAWBERRY", 202, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"BSHORT_WEMP_PLANT", 203, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"BSHORT_RICE", 204, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"BMED_BLUEBERRY", 205, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"BMED_CORN", 206, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BMED_COTTON", 207, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"BMED_HAZELNUTS", 208, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"BMED_LINGONBERRY", 209, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"BMED_MUSHROOM_BLACK", 210, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_MUSHROOM_BLUE", 211, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_MUSHROOM_BROWN", 212, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_MUSHROOM_GREEN", 213, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_MUSHROOM_RED", 214, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_MUSHROOM_YELLOW", 215, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BMED_ONION", 216, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BMED_POTATO", 217, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BMED_PUMPKIN", 218, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BMED_STRAWBERRY", 219, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"BMED_WEMP_PLANT", 220, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"BMED_RICE", 221, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20},
                new Object[]{"BTALL_BLUEBERRY", 222, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 571, 364, (byte) 0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0},
                new Object[]{"BTALL_CORN", 223, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 32, (byte) 0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BTALL_COTTON", 224, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 570, 144, (byte) 0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12},
                new Object[]{"BTALL_HAZELNUTS", 225, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 570, 134, (byte) 0, 8, 8, -5, -5, ModifiedBy.HUNGER, 24},
                new Object[]{"BTALL_LINGONBERRY", 226, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 571, 367, (byte) 0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12},
                new Object[]{"BTALL_MUSHROOM_BLACK", 227, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 247, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_MUSHROOM_BLUE", 228, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 250, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_MUSHROOM_BROWN", 229, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 248, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_MUSHROOM_GREEN", 230, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 246, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_MUSHROOM_RED", 231, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 251, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_MUSHROOM_YELLOW", 232, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 249, (byte) 0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12},
                new Object[]{"BTALL_ONION", 233, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 355, (byte) 0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BTALL_POTATO", 234, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 35, (byte) 0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BTALL_PUMPKIN", 235, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 33, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0},
                new Object[]{"BTALL_STRAWBERRY", 236, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 571, 362, (byte) 0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20},
                new Object[]{"BTALL_WEMP_PLANT", 237, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 570, 316, (byte) 0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 0},
                new Object[]{"BTALL_RICE", 238, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 569, 746, (byte) 0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20}
                //</editor-fold>
        ));
    }
}
