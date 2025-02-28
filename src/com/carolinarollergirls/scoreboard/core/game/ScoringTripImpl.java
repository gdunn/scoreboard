package com.carolinarollergirls.scoreboard.core.game;

import com.carolinarollergirls.scoreboard.core.interfaces.Clock;
import com.carolinarollergirls.scoreboard.core.interfaces.Game;
import com.carolinarollergirls.scoreboard.core.interfaces.ScoringTrip;
import com.carolinarollergirls.scoreboard.core.interfaces.TeamJam;
import com.carolinarollergirls.scoreboard.event.Command;
import com.carolinarollergirls.scoreboard.event.NumberedScoreBoardEventProviderImpl;
import com.carolinarollergirls.scoreboard.event.Value;
import com.carolinarollergirls.scoreboard.rules.Rule;

public class ScoringTripImpl extends NumberedScoreBoardEventProviderImpl<ScoringTrip> implements ScoringTrip {
    ScoringTripImpl(TeamJam parent, int number) {
        super(parent, number, TeamJam.SCORING_TRIP);
        game = parent.getTeam().getGame();
        addProperties(props);
        setCopy(JAM_CLOCK_START, this, PREVIOUS, JAM_CLOCK_END, true);
        setRecalculated(DURATION).addSource(this, JAM_CLOCK_END).addSource(this, JAM_CLOCK_START);
        set(AFTER_S_P, hasPrevious() ? getPrevious().get(AFTER_S_P) : false);
    }

    @Override
    public Object computeValue(Value<?> prop, Object value, Object last, Source source, Flag flag) {
        if (prop == SCORE && (Integer) value < 0) { return 0; }
        if (prop == DURATION) {
            if (get(JAM_CLOCK_END) > 0L) {
                return get(JAM_CLOCK_END) - get(JAM_CLOCK_START);
            } else {
                return 0L;
            }
        }
        return value;
    }
    @Override
    public void valueChanged(Value<?> prop, Object value, Object last, Source source, Flag flag) {
        if ((prop == SCORE || (prop == CURRENT && !(Boolean) value)) && get(JAM_CLOCK_END) == 0L &&
            game.getClock(Clock.ID_JAM) != null) {
            set(JAM_CLOCK_END, game.getClock(Clock.ID_JAM).getTimeElapsed());
        }
        if (prop == SCORE && source == Source.WS && game.getBoolean(Rule.WFTDA_LATE_SCORE_RULE)) {
            TeamJam tj = (TeamJam) parent;
            boolean nextJamNotStarted = tj.getJam() == game.getCurrentPeriod().getCurrentJam();
            boolean nextJamNotEnded =
                nextJamNotStarted ||
                (game.getCurrentPeriod().getCurrentJam() == tj.getJam().getNext() && game.isInJam());
            boolean lastTwoMinutes = (game.getClock(Clock.ID_PERIOD).getTimeRemaining() < 120000 &&
                                      game.getCurrentPeriodNumber() == game.getInt(Rule.NUMBER_PERIODS));
            boolean changeOk = ((!lastTwoMinutes && nextJamNotEnded) || (lastTwoMinutes && nextJamNotStarted)) &&
                               !game.isOfficialScore();
            if (!changeOk) {
                int change = (Integer) value - (Integer) last;
                tj.set(TeamJam.OS_OFFSET, -change, Flag.CHANGE);
            }
        }
        if (prop == CURRENT && (Boolean) value && get(SCORE) == 0) { set(JAM_CLOCK_END, 0L); }
        if (prop == AFTER_S_P) {
            if ((Boolean) value && hasNext()) { getNext().set(AFTER_S_P, true); }
            if (!(Boolean) value && hasPrevious()) { getPrevious().set(AFTER_S_P, false); }
            if (flag != Flag.SPECIAL_CASE) {
                if ((Boolean) value && (!hasPrevious() || !getPrevious().get(AFTER_S_P))) {
                    parent.set(TeamJam.STAR_PASS_TRIP, this);
                } else if (!(Boolean) value && (!hasNext() || getNext().get(AFTER_S_P))) {
                    parent.set(TeamJam.STAR_PASS_TRIP, getNext());
                }
            }
        }
    }

    @Override
    public void execute(Command prop, Source source) {
        if (prop == REMOVE) {
            if (getParent().numberOf(TeamJam.SCORING_TRIP) > 1) {
                delete(source);
            } else {
                // We cannot remove the initial trip when it is the only trip, so set its score
                // to 0.
                set(SCORE, 0);
                set(JAM_CLOCK_END, 0L);
                set(ANNOTATION, "");
            }
        } else if (prop == INSERT_BEFORE) {
            parent.add(ownType, new ScoringTripImpl((TeamJam) parent, getNumber()));
        }
    }

    @Override
    public int getScore() {
        return get(SCORE);
    }

    @Override
    public boolean isAfterSP() {
        return get(AFTER_S_P);
    }

    @Override
    public String getAnnotation() {
        return get(ANNOTATION);
    }

    private Game game;
}
