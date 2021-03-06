package com.joedobo27.wurminianpacifist;


import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.skills.Skills;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.joedobo27.wurminianpacifist.Wrap.Actions.*;
import static com.joedobo27.wurminianpacifist.Wrap.Rarity.*;


class CreateEssenceAction implements ModAction, BehaviourProvider, ActionPerformer {
    private static final Logger logger = Logger.getLogger(CreateEssenceAction.class.getName());

    private final short actionId;
    private final ActionEntry actionEntry;
    private final float CONVERT_TIME_TO_COUNTER_DIVISOR = 10.0f;
    private static WeakHashMap<Action, EssenceActionData> actionListener;

    @Deprecated
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

    private class EssenceActionData {

        ArrayList<Item> gooItems;
        //HashMap<DataGroup, ArrayList<Item>> groupedRareItems; // items of rarity grouped by templateId and rarity.
        HashMap<Integer, ArrayList<Item>> rareItems;
        Item gemActive;
        int unitsToMake;

        EssenceActionData(Item gemActive, Item AlembicTarget) {
            this.gemActive = gemActive;
            gooItems = getDullGooItems(AlembicTarget);
            //groupedRareItems = new HashMap<>();
            //groupRareItems(amphoraTarget);
            rareItems = new HashMap<>();
            groupRareItemsByTemplate(AlembicTarget);
            unitsToMake = getSmallestUnit();
        }

        /*
        private void groupRareItems(Item container) {
            ArrayList<Item> items = getRareItems(container);
            for (Item item : items) {
                DataGroup data2 = new DataGroup(item.getTemplateId(), item.getRarity());
                if ((int)groupedRareItems.keySet().stream().filter(value -> value.equals(data2)).count() == 0) {
                    groupedRareItems.put(data2, new ArrayList<>(Collections.singletonList(item)));
                }
                DataGroup data3 = groupedRareItems.keySet().stream().filter(value -> value.equals(data2)).findFirst().orElse(null);
                if (data3 != null)
                    groupedRareItems.get(data3).add(item);
            }
        }
        */

        private void groupRareItemsByTemplate(Item container) {
            ArrayList<Item> items = getRareItems(container);
            logger.log(Level.INFO, "found items " + items.toString());
            for (Item item : items) {
                if ((int)rareItems.keySet().stream().filter(templateId -> templateId == item.getTemplateId()).count() == 0) {
                    rareItems.put(item.getTemplateId(), new ArrayList<>(Collections.singletonList(item)));
                }
                else {
                    Integer index = rareItems.keySet().stream().filter(templateId -> templateId == item.getTemplateId()).findFirst().orElse(null);
                    if (index != null)
                        rareItems.get(index).add(item);
                }
            }
            logger.log(Level.INFO, "rare items " + rareItems.toString());
        }

        private ArrayList<Item> getRareItems(Item container) {
            Item[] contents = container.getAllItems(false);
            return Arrays.stream(contents)
                    .filter(item -> item.getRarity() > NO_RARITY.getId() && item.getTemplateId() != WurminianPacifistMod.getDullGooTemplateId() &&
                    item.getWeightGrams() == item.getTemplate().getWeightGrams())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Item> getDullGooItems(Item container) {
            Item[] contents = container.getAllItems(false);
            return Arrays.stream(contents)
                    .filter(item -> item.getTemplateId() == WurminianPacifistMod.getDullGooTemplateId() &&
                            item.getWeightGrams() == item.getTemplate().getWeightGrams())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private float getRareUnitCount() {
            if (rareItems == null)  {
                return 0;
            }
            return (float) rareItems.values().stream()
                    .mapToDouble((ArrayList<Item> items) -> getTotalUnitCount(items.toArray(new Item[items.size()])))
                    .sum();
        }

        private float getGooUnitCount() {
            if (gooItems == null) {
                return 0;
            }
            return getTotalUnitCount(gooItems.toArray(new Item[gooItems.size()]));
        }

        private float getGemUnitCount() {
            return gemActive.getQualityLevel() / WurminianPacifistMod.getGemQualityPer();
        }

        private int getSmallestUnit() {
            float[] floats = new float[]{getRareUnitCount(), getGooUnitCount(), getGemUnitCount()};
            Arrays.sort(floats);
            return (int) floats[0];
        }

        /**
         * Sum weight and divided by standard template weight to get a unit count. In WU volume doesn't change for partials.
         *
         * @param sameTypeItems array of WU Item objects type.
         * @return float type.
         */
        private float getTotalUnitCount(Item[] sameTypeItems){
            if (sameTypeItems.length == 0)
                return 0.0f;
            int itemTemplateID = sameTypeItems[0].getTemplateId();
            boolean identicalItemTemplates = 0 == Arrays.stream(sameTypeItems)
                    .filter(value -> value.getTemplateId() != itemTemplateID)
                    .count();
            if (!identicalItemTemplates){
                throw new RuntimeException("The arg sameTypeItems must contain items of same templateID.");
            }
            int totalGrams = Arrays.stream(sameTypeItems)
                    .mapToInt(Item::getWeightGrams)
                    .sum();
            return (float) totalGrams / (float) sameTypeItems[0].getTemplate().getWeightGrams();
        }

        private boolean contentsEquals(Item container) {
            ArrayList<Item> raresNow = getRareItems(container);
            ArrayList<Item> gooNow = getDullGooItems(container);
            ArrayList<Item> itemsNow = new ArrayList<>();
            itemsNow.addAll(raresNow);
            itemsNow.removeAll(gooNow);
            itemsNow.addAll(gooNow);


            ArrayList<Item> raresWas = new ArrayList<>();
            rareItems.values().forEach(raresWas::addAll);
            ArrayList<Item> gooWas = new ArrayList<>(gooItems);
            ArrayList<Item> itemsWas = new ArrayList<>();
            itemsWas.addAll(raresWas);
            itemsWas.addAll(gooWas);

            itemsWas.removeAll(itemsNow);

            return itemsWas.size() == 0;

        }

        private ArrayList<Item> destroyGoo(int unitsToMake) {
            Collections.shuffle(gooItems);

            List<Item> view = gooItems.subList(0, unitsToMake);
            ArrayList<Item> removed = new ArrayList<>(view);
            view.clear();
            gooItems.removeAll(removed);
            return removed;
        }

        private ArrayList<Item> destroyRares(int unitsToMake) {
            ArrayList<Item> removed = new ArrayList<>();

            ArrayList<Integer> keys = rareItems.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(keys);

            for (Integer key : keys) {
                Collections.shuffle(rareItems.get(key));
                ArrayList arrayList = rareItems.get(key);
                for (Object anArrayList : arrayList) {
                    removed.add((Item) anArrayList);
                    if ( removed.size() == unitsToMake)
                        break;
                }
                if ( removed.size() == unitsToMake)
                    break;
            }
            for (Integer key : keys) {
                rareItems.get(key).removeAll(removed);
            }
            return removed;
        }
    }

    CreateEssenceAction() {
        actionListener = new WeakHashMap<>();
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Extract", "extracting", new int[]{POLICED.getId(), NON_RELIGION.getId(),
        ALLOW_FO.getId(), ALWAYS_USE_ACTIVE_ITEM.getId()});
        ModActions.registerAction(actionEntry);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
        if (activeItemIsCatalyst(subject) && target.getTemplateId() == WurminianPacifistMod.getPotteryAlembicTemplateId()){
            return Collections.singletonList(actionEntry);
        } else
            return null;
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    @Override
    public boolean action(Action action, Creature performer, Item gemActive, Item AlembicTarget, short num, float counter) {
        if (performer instanceof Player && AlembicTarget != null && activeItemIsCatalyst(gemActive) &&
                AlembicTarget.getTemplateId() == WurminianPacifistMod.getPotteryAlembicTemplateId()) {
            try {
                int time;
                final float ACTION_START_TIME = 1.0f;

                WeakReference<Action> weakActionReference;
                EssenceActionData essenceActionData;
                if (counter == ACTION_START_TIME) {
                    essenceActionData = new EssenceActionData(gemActive, AlembicTarget);
                    actionListener.put(action, essenceActionData);

                    if (!requiredMaterialsInContainer(essenceActionData, performer, AlembicTarget))
                        return true;

                    performer.getCommunicator().sendNormalServerMessage("You start " + action.getActionEntry().getVerbString() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to " + action.getActionString() + ".", performer, 5);


                    time = WurminianPacifistMod.getBaseActionTime() * essenceActionData.unitsToMake;
                    performer.getCurrentAction().setTimeLeft(time);
                    performer.sendActionControl(action.getActionEntry().getVerbString(), true, time);
                    return false;
                } else {
                    essenceActionData = actionListener.get(action);
                    if (isActionDone(performer.getCurrentAction().getTimeLeft(), counter) && essenceActionData != null) {
                        if (!requiredMaterialsInContainer(essenceActionData, performer, AlembicTarget))
                            return true;
                        int unitsToMake = essenceActionData.unitsToMake;
                        // Damage gem
                        gemActive.setQualityLevel(gemActive.getQualityLevel() - (unitsToMake * WurminianPacifistMod.getGemQualityPer()));
                        // Destroy goo in the proper proportions.
                        ArrayList<Item> destroyedGoos = essenceActionData.destroyGoo(unitsToMake);
                        destroyedGoos.forEach(item -> item.setWeight(item.getWeightGrams() - item.getTemplate().getWeightGrams(), true));
                        // Destroy rares in the proper proportions.
                        ArrayList<Item> destroyedRares = essenceActionData.destroyRares(unitsToMake);

                        // do skill check for Natural substances
                        Skills skills = performer.getSkills();
                        Skill natural_substances = skills.getSkillOrLearn(SkillList.ALCHEMY_NATURAL);
                        Skill mind_logic = skills.getSkillOrLearn(SkillList.MIND_LOGICAL);
                        double power = natural_substances.skillCheck(1, AlembicTarget, mind_logic.getKnowledge() / 5, false, counter);
                        // create essence in container.
                        for (Item item : destroyedRares) {
                            Item essence = null;
                            try {
                                essence = ItemFactory.createItem(WurminianPacifistMod.getEssenceTemplateId(), (float) Math.max(power, 1.0f), (byte) 21, null);
                            } catch (NoSuchTemplateException e1) {
                                logger.log(Level.WARNING, e1.getMessage(), e1);
                            } catch (FailedException e2) {
                                logger.log(Level.WARNING, "Failed to create item.", e2);
                            }
                            int weightMultiplier = 1;
                            switch (item.getRarity()) {
                                case 1:
                                    weightMultiplier = 1;
                                    break;
                                case 2:
                                    weightMultiplier = 34; // SUPREME
                                    break;
                                case 3:
                                    weightMultiplier = 9709; // FANTASTIC
                                    break;
                            }
                            if (essence != null) {
                                essence.setWeight(essence.getTemplate().getWeightGrams() * weightMultiplier, true);
                                essence.setRarity((byte) 0);
                                essence.setParentId(AlembicTarget.getWurmId(), false);
                                AlembicTarget.insertItem(essence, true);
                            }
                        }

                        destroyedRares.forEach(item -> item.setWeight(item.getWeightGrams() - item.getTemplate().getWeightGrams(), true));
                        return true;
                    }
                }
            } catch (NoSuchActionException e) {
                CreateEssenceAction.logger.log(Level.INFO, "This action does not exist?", e);
            }
        }
        return false;
    }

    private boolean requiredMaterialsInContainer(EssenceActionData essenceActionData, Creature performer, Item container) {
        if (essenceActionData.getRareUnitCount() < 1) {
            performer.getCommunicator().sendNormalServerMessage("The vessel needs at least one rare item to extract from.");
            return false;
        }

        if (essenceActionData.getGooUnitCount() < 1) {
            performer.getCommunicator().sendNormalServerMessage("The vessel needs at least one dull goo for the reaction.");
            return false;
        }
        if (!essenceActionData.contentsEquals(container)) {
            performer.getCommunicator().sendNormalServerMessage("Removing items stops the conversion.");
            return false;
        }
        if (essenceActionData.gemActive.getQualityLevel() < WurminianPacifistMod.getGemQualityPer()) {
            performer.getCommunicator().sendNormalServerMessage("The gem is of too low quality.");
            return false;
        }
        return true;
    }

    boolean isActionInProgress(int time, float counter){
        return time - (counter * CONVERT_TIME_TO_COUNTER_DIVISOR) > 1f;
    }

    private boolean isActionDone(int time, float counter){
        return time - (counter * CONVERT_TIME_TO_COUNTER_DIVISOR) <= 0f;
    }

    private boolean activeItemIsCatalyst(Item active){
        return active.isGem() && !active.isSource();
    }
}
