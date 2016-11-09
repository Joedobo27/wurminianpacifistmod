package com.Joedobo27.wurminianpacifist;


import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.Joedobo27.wurminianpacifist.Wrap.Actions.*;
import static com.Joedobo27.wurminianpacifist.Wrap.Rarity.*;


class CreateEssenceAction implements ModAction, BehaviourProvider, ActionPerformer {
    private static final Logger logger = Logger.getLogger(CreateEssenceAction.class.getName());

    private final short actionId;
    private final ActionEntry actionEntry;

    private ArrayList<Item> gooItems = new ArrayList<>();
    private float gooUnitCount;
    private ArrayList<Item> rareItems = new ArrayList<>();
    private ArrayList<Item> itemsToDelete = new ArrayList<>();
    private float gemUnitCount;
    private int unitsToMake;

    private class DataGroup {
        int templateId;
        int rarity;

        DataGroup(int templateId, int rarity) {
            this.templateId = templateId;
            this.rarity = rarity;
        }

        public boolean equals(int templateId, int rarity) {
            return this.templateId == templateId && this.rarity == rarity;
        }

        boolean equals(DataGroup dataGroup) {
            return this.templateId == dataGroup.templateId && this.rarity == dataGroup.rarity;
        }
    }

    private class essenceActionData {

        ArrayList<Item> gooItems;
        HashMap<DataGroup, ArrayList<Item>> groupedRareItems; // items of rarity grouped by templateId and rarity.
        Item gemActive;

        essenceActionData(Item gemActive, Item amphoraTarget) {
            this.gemActive = gemActive;
            gooItems = containsDullGoo(amphoraTarget);
            groupRareItems(amphoraTarget);
            groupedRareItems = new HashMap<>();
        }

        private void groupRareItems(Item container) {
            Item[] contents = container.getAllItems(false);
            ArrayList<Item> items = Arrays.stream(contents)
                    .filter(value -> value.getRarity() > NO_RARITY.getId())
                    .collect(Collectors.toCollection(ArrayList::new));

            for (Item item : items) {
                DataGroup data2 = new DataGroup(item.getTemplateId(), item.getRarity());
                if ((int)groupedRareItems.keySet().stream().filter(value -> value.equals(data2)).count() == 0) {
                    groupedRareItems.put(data2, new ArrayList<>(Arrays.asList(item)));
                }
                DataGroup data3 = groupedRareItems.keySet().stream().filter(value -> value.equals(data2)).findFirst().orElse(null);
                if (data3 != null)
                    groupedRareItems.get(data3).add(item);
                }
        }

        private ArrayList<Item> containsDullGoo(Item container) {
            Item[] contents = container.getAllItems(false);
            ArrayList<Item> toReturn = new ArrayList<>(5);
            for (Item item:contents){
                if (item.getTemplateId() == WurminianPacifistMod.getDullGooID())
                    toReturn.add(item);
            }
            return toReturn;
        }
    }

    public CreateEssenceAction() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Extract", "extracting", new int[]{POLICED.getId(), NON_RELIGION.getId(),
        ALLOW_FO.getId(), ALWAYS_USE_ACTIVE_ITEM.getId()});
        ModActions.registerAction(actionEntry);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
        if (activeItemIsCatalyst(subject) && target.getTemplateId() == ItemList.amphoraLargePottery){
            return Arrays.asList(actionEntry);
        } else
            return null;
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    @Override
    public boolean action(Action action, Creature performer, Item gemActive, Item amphoraTarget, short num, float counter) {
        if (performer instanceof Player && amphoraTarget != null && gemActive.isGem() &&
                amphoraTarget.getTemplateId() == ItemList.amphoraLargePottery) {
            try {
                final float ACTION_START_TIME = 1.0f;
                final float CONVERT_TIME_TO_DIVISOR = 10.0f;
                essenceActionData essenceActionData;
                if (counter == ACTION_START_TIME) {
                    if (!activeItemIsCatalyst(gemActive)) {
                        performer.getCommunicator().sendNormalServerMessage("You need to use a gem.");
                        return true;
                    }
                    essenceActionData = new essenceActionData(gemActive, amphoraTarget);

                    gooUnitCount = getTotalUnitCount((Item[]) gooItems.toArray());
                    gemUnitCount = gemActive.getQualityLevel() / 10;
                    if (!requiredMaterialsInContainer(amphoraTarget, performer))
                        return true;
                    unitsToMake = findSmallestUnit(gemUnitCount, gooUnitCount);
                    performer.getCommunicator().sendNormalServerMessage("You start " + action.getActionEntry().getVerbString() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to " + action.getActionString() + ".", performer, 5);

                    final int time = 50;
                    performer.getCurrentAction().setTimeLeft(time);
                    performer.sendActionControl(action.getActionEntry().getVerbString(), true, time);
                    return false;
                }

                float performerTimeMinusActionCounter = (float) performer.getCurrentAction().getTimeLeft() - (counter * CONVERT_TIME_TO_DIVISOR);
                if (isActionDone(performerTimeMinusActionCounter)) {
                    if (!activeItemIsCatalyst(gemActive)) {
                        performer.getCommunicator().sendNormalServerMessage("Where did the gem go?");
                        return true;
                    }
                    if (!requiredMaterialsInContainer(amphoraTarget, performer))
                        return true;
                    // Damage gem
                    gemActive.setQualityLevel(gemActive.getQualityLevel() - (unitsToMake * 10.0f));
                    // Destroy goo in the proper proportions.
                    destroyItemsInProportion(unitsToMake, gooItems);
                    // Destroy rares in the proper proportions.
                    int raresTally;
                    int makeCount = unitsToMake;
                    for (Data data : datas) {
                        raresTally = destroyItemsInProportion(makeCount, data.getItems());
                        makeCount = -raresTally;
                        if (makeCount == 0)
                            break;
                    }
                    // create essence in container.

                }
            } catch (NoSuchActionException e) {
                CreateEssenceAction.logger.log(Level.INFO, "This action does not exist?", e);
            }
        }
        return false;
    }

    private int destroyItemsInProportion(int makeUnits, ArrayList<Item> items) {
        int wholeUniteGrams = Wrap.getTemplateWeightFromItem(items.get(0));
        int gramsTotal = makeUnits * wholeUniteGrams;
        int gramsTally = 0;
        int excess = 0;
        for (Item item : items) {
            gramsTally =+ item.getWeightGrams();
            if (gramsTally < gramsTotal) {
                itemsToDelete.add(item);
            } else if (gramsTally == gramsTotal) {
                itemsToDelete.add(item);
            }
            if (gramsTally > gramsTotal) {
                // do partial delete on item.
                excess = gramsTally - gramsTotal;
                item.setWeight(excess, true);
                break;
            }
        }
        return (gramsTally - excess) / wholeUniteGrams;
    }

    private int findSmallestUnit(float gemUnitCount, float gooUnitCount){
        int gem = (int) Math.floor(gemUnitCount);
        int goo = (int) Math.floor(gooUnitCount);
        int[] rares = datas.stream()
                .mapToInt(value -> (int) Math.floor(value.getWholeUnits()))
                .toArray();
        int raresSum = Arrays.stream(rares)
                .sum();
        int[] b = new int[]{gem, goo, raresSum};
        Arrays.sort(b);
        return b[0];
    }

    private boolean requiredMaterialsInContainer(Item target, Creature performer) {
        if (rareItems.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs a rare item to extract from.");
            return false;
        }
        if (gooItems.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs dull goo for the reaction.");
            return false;
        }
        if (0 == datas.stream()
                .filter(value -> value.getWholeUnits() > 1.0f)
                .count()){
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs at least one whole rare item.");
            return false;
        }
        if (1.0f > gooUnitCount) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs at least one whole dull goo.");
            return false;
        }
        if (0 < rareItems.stream()
                .filter(value -> value.getParentId() != target.getWurmId())
                .count()) {
            performer.getCommunicator().sendNormalServerMessage("Removing items stops the conversion.");
            return false;
        }
        if (0 < gooItems.stream()
                .filter(value -> value.getParentId() != target.getWurmId())
                .count()){
            performer.getCommunicator().sendNormalServerMessage("Removing items stops the conversion.");
            return false;
        }
        return true;
    }

    /**
     * Sum weight and divided by standard template weight to get a unit count. In WU volume doesn't change for partials.
     *
     * @param sameTypeItems array of WU Item objects type.
     * @return float type.
     */
    private float getTotalUnitCount(Item[] sameTypeItems){
        int itemTemplateID = sameTypeItems[0].getTemplateId();
        boolean identicalItemTemplates = 0 == Arrays.stream(sameTypeItems)
                .filter(value -> value.getTemplateId() != itemTemplateID)
                .count();
        if (!identicalItemTemplates){
            throw new RuntimeException("The arg sameTypeItems must container items of same templateID.");
        }
        int totalGrams = Arrays.stream(sameTypeItems)
                .mapToInt(Item::getWeightGrams)
                .sum();
        return (float) totalGrams / (float) Wrap.getTemplateWeightFromItem(sameTypeItems[0]);
    }

    boolean isActionInProgress(float timeDifference){
        return timeDifference > 1f;
    }

    private boolean isActionDone(float timeDifference){
        return timeDifference <= 0f;
    }

    private boolean activeItemIsCatalyst(Item active){
        return active.isGem() && !active.isSource();
    }
}
