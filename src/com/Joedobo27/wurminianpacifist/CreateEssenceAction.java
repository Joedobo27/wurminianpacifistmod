package com.Joedobo27.wurminianpacifist;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.Joedobo27.wurminianpacifist.CreateEssenceAction.Actions.*;
import static com.Joedobo27.wurminianpacifist.CreateEssenceAction.Rarity.*;

public class CreateEssenceAction implements ModAction, BehaviourProvider, ActionPerformer {
    private static final Logger logger = Logger.getLogger(CreateEssenceAction.class.getName());

    private final short actionId;
    private final ActionEntry actionEntry;

    private ArrayList<Item> gooItems = new ArrayList<>();
    private float gooUnitCount;
    private ArrayList<Item> rareItems = new ArrayList<>();
    private HashMap<Integer, ArrayList<Item>> groupsPerTemplateID;
    private HashMap<Integer, Float> unitCountPerTemplateIDGroup;
    private final float ACTION_START_TIME = 1.0f;

    @SuppressWarnings("unused")
    enum Actions {
        QUICK(0,""),
        NEED_FOOD(1,"Action blocked if food is too low."),
        SPELL(2,""),
        ATTACK(3,""),
        FATIGUE(4,""),
        POLICED(5,""),
        NOMOVE(6,"Actions is cancelled if toon moves."),
        NON_LIBILAPRIEST(7,""),
        NON_WHITEPRIEST(8,""),
        NON_RELIGION(9,"Doing Actions are considered unfaithful."),
        ATTACK_HIGH(12,""),
        ATTACK_LOW(13,""),
        ATTACK_LEFT(14,""),
        ATTACK_RIGHT(15,""),
        DEFEND(16,""),
        STANCE_CHANGE(17,""),
        ALLOW_MAGRANON(18,""),
        ALLOW_FO(19,""),
        ALLOW_VYNORA(20,""),
        ALLOW_LIBILA(21,""),
        NO_OPPORTUNITY(22,""),
        IGNORERANGE(23,""),
        VULNERABLE(24,""),
        MISSION(25,""),
        NOTVULNERABLE(26,""),
        NONSTACKABLE(27,""),
        NONSTACKABLE_FIGHT(28,""),
        BLOCKED_NONE(29,""),
        BLOCKED_FENCE(30,""),
        BLOCKED_WALL(31,""),
        BLOCKED_FLOOR(32,""),
        BLOCKED_ALL_BUT_OPEN(33,""),
        BLOCKED_TARGET_TILE(34,""),
        MAYBE_USE_ACTIVE_ITEM(35,""),
        ALWAYS_USE_ACTIVE_ITEM(36,""),
        NEVER_USE_ACTIVE_ITEM(37,""),
        ALLOW_MAGRANON_IN_CAVE(38,""),
        ALLOW_FO_ON_SURFACE(39,""),
        ALLOW_LIBILA_IN_CAVE(40,""),
        USES_NEW_SKILL_SYSTEM(41,""),
        VERIFIED_NEW_SKILL_SYSTEM(42,""),
        SHOW_ON_SELECT_BAR(43,""),
        SAME_BRIDGE(44,""),
        PERIMETER(45,""),
        CORNER(46,""),
        ENEMY_NEVER(47,""),
        ENEMY_ALWAYS(48,""),
        ENEMY_NO_GUARDS(49,""),
        BLOCKED_NOT_DOOR(50,"");

        private final int id;
        private final String description;

        Actions(int actionID, String description){
            this.id = actionID;
            this.description = description;
        }

        public int getId() {
            return id;
        }
    }

    @SuppressWarnings("unused")
    enum Rarity {
        NO_RARITY(0),
        RARE(1),
        SUPREME(2),
        FANTASTIC(3);

        private final int id;

        Rarity(int id){
            this.id = id;
        }

        public byte getId() {
            return (byte)id;
        }
    }

    public CreateEssenceAction() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Extract", "extracting", new int[]{POLICED.getId(), NON_RELIGION.getId(),
        ALLOW_FO.getId(), ALWAYS_USE_ACTIVE_ITEM.getId()});
        ModActions.registerAction(actionEntry);
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return this;
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return this;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        return getBehavioursFor(performer, null, target);
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
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return action(action, performer, null, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        try {
            if (counter == ACTION_START_TIME) {
                if (!activeItemIsCatalyst(source)) {
                    performer.getCommunicator().sendNormalServerMessage("You need to use a gem.");
                    return true;
                }
                if (!requiredMaterialsInContainer(counter, target, performer))
                    return true;
            }
            float CONVERT_TIME_UNITS = 10.0f;
            float performerTimeMinusActionCounter = (float) performer.getCurrentAction().getTimeLeft() - (counter * CONVERT_TIME_UNITS);
            if (isActionDone(performerTimeMinusActionCounter)) {
                if (!activeItemIsCatalyst(source)) {
                    performer.getCommunicator().sendNormalServerMessage("Where did the gem go?");
                    return true;
                }
                if (!requiredMaterialsInContainer(counter, target, performer))
                    return true;
                // Damage gem

                // destroy rares and goo in the proper proportions.

                // create essence in container.
            }
        }catch (NoSuchActionException e){
            CreateEssenceAction.logger.log(Level.INFO, "This action does not exist?", e);
        }
       return false;
    }

    private boolean requiredMaterialsInContainer(float counter, Item target, Creature performer) {
        if (counter == ACTION_START_TIME){
            rareItems = containsRareItem(target);
            gooItems = containsDullGoo(target);
            groupsPerTemplateID = groupByItemTypes(rareItems);
            unitCountPerTemplateIDGroup = unitTotalsPerGroup(groupsPerTemplateID);
            gooUnitCount = getTotalUnitCount((Item[])gooItems.toArray());

        }
        if (rareItems.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs a rare item to extract from.");
            return false;
        }
        if (gooItems.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs dull goo for the reaction.");
            return false;
        }
        if (0 == unitCountPerTemplateIDGroup.values().stream()
                .filter(value -> value > 1.0f)
                .count()){
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs at least one whole rare item.");
        }
        if (1.0f > gooUnitCount) {
            performer.getCommunicator().sendNormalServerMessage("Your vessel needs at least one whole dull goo.");
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

    private HashMap<Integer, Float> unitTotalsPerGroup(HashMap<Integer, ArrayList<Item>> groups){
        // Initialize the groups.
        HashMap<Integer, Float> toReturn = new HashMap<>(groups.size());
        // Sum the grams in groups.
        for (Map.Entry a :groups.entrySet()){
            Item[] items = (Item[])a.getValue();
            toReturn.put((int)a.getKey(), getTotalUnitCount(items));
        }
        return toReturn;
    }

    private HashMap<Integer, ArrayList<Item>> groupByItemTypes(ArrayList<Item> items){
        int[] distinctTypes =items.stream()
                .mapToInt(Item::getTemplateId)
                .distinct()
                .toArray();
        // Initialize the groups.
        HashMap<Integer, ArrayList<Item>> toReturn = new HashMap<>(distinctTypes.length);
        for (int templateID:distinctTypes){
            toReturn.put(templateID, new ArrayList<>());
        }
        // Divided up into the groups.
        for (Item item:items){
            for (int templateID:distinctTypes) {
                if (item.getTemplateId() == templateID){
                    toReturn.get(templateID).add(item);
                }
            }
        }
        return toReturn;
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
        return (float) totalGrams / (float) sameTypeItems[0].getTemplate().getWeightGrams();
    }

    @SuppressWarnings("unused")
    boolean isActionInProgress(float timeDifference){
        return timeDifference > 1f;
    }

    private boolean isActionDone(float timeDifference){
        return timeDifference <= 0f;
    }

    private boolean activeItemIsCatalyst(Item active){
        return active.isGem() && !active.isSource();
    }

    private ArrayList<Item> containsRareItem(Item container) {
        Item[] contents = container.getAllItems(false);
        ArrayList<Item> toReturn = new ArrayList<>(5);
        for (Item item:contents){
            if (item.getRarity() > NO_RARITY.getId())
                toReturn.add(item);
        }
        return toReturn;
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
