package com.carolinarollergirls.scoreboard.core;
/**
 * Copyright (C) 2008-2012 Mr Temper <MrTemper@CarolinaRollergirls.com>
 *
 * This file is part of the Carolina Rollergirls (CRG) ScoreBoard.
 * The CRG ScoreBoard is licensed under either the GNU General Public
 * License version 3 (or later), or the Apache License 2.0, at your option.
 * See the file COPYING for details.
 */

import com.carolinarollergirls.scoreboard.event.ScoreBoardEvent.CommandProperty;
import com.carolinarollergirls.scoreboard.event.ScoreBoardEvent.PermanentProperty;
import com.carolinarollergirls.scoreboard.event.ScoreBoardEventProvider;

public interface Position extends ScoreBoardEventProvider {
    public void reset();

    public Team getTeam();
    public String getId();
    public FloorPosition getFloorPosition();
    public Skater getSkater();
    public void setSkater(Skater s);
    public boolean isPenaltyBox();
    public void setPenaltyBox(boolean box);

    public enum Value implements PermanentProperty {
	ID,
	SKATER,
	NAME,
	NUMBER,
	FLAGS,
	PENALTY_BOX;
    }
    public enum Command implements CommandProperty {
	CLEAR;
    }
}
