package forge.ai;

import forge.game.card.Card;
import forge.game.card.CardCollection;
import forge.game.card.CardCollectionView;
import forge.game.card.CardLists;
import forge.game.card.CardPredicates;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;

public final class ComputerUtilCombo {
    private static final String FELIDAR_GUARDIAN = "Felidar Guardian";
    private static final String SAHEELI_RAI = "Saheeli Rai";

    private ComputerUtilCombo() {
    }

    public static Card getSaheeliFelidarCopyTarget(final String sourceName, final CardCollectionView choices) {
        if (!SAHEELI_RAI.equals(sourceName)) {
            return null;
        }

        final CardCollection felidarGuardians = CardLists.filter(choices, CardPredicates.nameEquals(FELIDAR_GUARDIAN));
        return felidarGuardians.isEmpty() ? null : felidarGuardians.getFirst();
    }

    public static Card getSaheeliFelidarBlinkTarget(final Player ai, final SpellAbility sa, final CardCollectionView choices) {
        if (!FELIDAR_GUARDIAN.equals(sa.getHostCard().getName())) {
            return null;
        }

        final CardCollectionView saheelis = ai.getCardsIn(ZoneType.Battlefield, SAHEELI_RAI);
        if (saheelis.isEmpty()) {
            return null;
        }

        final Card saheeli = saheelis.getFirst();
        return choices.contains(saheeli) ? saheeli : null;
    }

    public static boolean shouldRepeatSaheeliFelidarBlink(final Player ai, final SpellAbility sa, final Card target) {
        if (target == null || !SAHEELI_RAI.equals(target.getName())
                || !FELIDAR_GUARDIAN.equals(sa.getHostCard().getName())) {
            return false;
        }

        return CardLists.filter(ai.getCardsIn(ZoneType.Battlefield), CardPredicates.nameEquals(FELIDAR_GUARDIAN)).size()
                < CardLists.filter(ai.getOpponents().getCardsIn(ZoneType.Battlefield), CardPredicates.CREATURES).size()
                        + ai.getOpponentsGreatestLifeTotal() + 10;
    }
}
