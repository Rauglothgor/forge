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

    public static Card getSaheeliFelidarDisruptionTarget(final Player ai, final Iterable<Card> choices) {
        Card felidar = null;
        for (final Card c : choices) {
            if (!c.getController().isOpponentOf(ai) || !opponentHasSaheeliFelidarCombo(ai, c.getController())) {
                continue;
            }
            if (SAHEELI_RAI.equals(c.getName())) {
                return c;
            }
            if (felidar == null && FELIDAR_GUARDIAN.equals(c.getName())) {
                felidar = c;
            }
        }
        return felidar;
    }

    public static boolean shouldCounterSaheeliFelidarComboPiece(final Player ai, final SpellAbility spellAbility) {
        if (spellAbility == null || !spellAbility.isSpell() || spellAbility.getHostCard() == null
                || !spellAbility.getActivatingPlayer().isOpponentOf(ai)) {
            return false;
        }

        final String name = spellAbility.getHostCard().getName();
        final Player opponent = spellAbility.getActivatingPlayer();
        if (SAHEELI_RAI.equals(name)) {
            return !opponent.getCardsIn(ZoneType.Battlefield, FELIDAR_GUARDIAN).isEmpty();
        }
        if (FELIDAR_GUARDIAN.equals(name)) {
            return !opponent.getCardsIn(ZoneType.Battlefield, SAHEELI_RAI).isEmpty();
        }
        return false;
    }

    private static boolean opponentHasSaheeliFelidarCombo(final Player ai, final Player opponent) {
        return opponent.isOpponentOf(ai)
                && !opponent.getCardsIn(ZoneType.Battlefield, SAHEELI_RAI).isEmpty()
                && !opponent.getCardsIn(ZoneType.Battlefield, FELIDAR_GUARDIAN).isEmpty();
    }
}
