package com.carolinarollergirls.scoreboard.core;
/**
 * Copyright (C) 2008-2012 Mr Temper <MrTemper@CarolinaRollergirls.com>
 *
 * This file is part of the Carolina Rollergirls (CRG) ScoreBoard.
 * The CRG ScoreBoard is licensed under either the GNU General Public
 * License version 3 (or later), or the Apache License 2.0, at your option.
 * See the file COPYING for details.
 */

import java.util.Map;

import com.carolinarollergirls.scoreboard.event.ScoreBoardEvent.AddRemoveProperty;
import com.carolinarollergirls.scoreboard.event.ScoreBoardEvent.PermanentProperty;
import com.carolinarollergirls.scoreboard.event.ScoreBoardEventProvider;
import com.carolinarollergirls.scoreboard.rules.Rule;

public interface Rulesets extends ScoreBoardEventProvider {
    public void reset();

    public void setCurrentRuleset(String id);

    // Get information from current ruleset.
    public Map<Rule, String> getAll();
    public String get(Rule r);
    public boolean getBoolean(Rule r);
    public int getInt(Rule r);
    public long getLong(Rule r);
    public void set(Rule r, String v);

    // The last loaded ruleset.
    public String getId();
    public void setId(String id);
    public String getName();
    public void setName(String n);
    
    public Rule getRule(String k);

    public Map<String, Ruleset> getRulesets();
    public Ruleset getRuleset(String id);
    public void removeRuleset(String id);
    public Ruleset addRuleset(String name, String parentId);
    public Ruleset addRuleset(String name, String parentId, String id);

    public enum Value implements PermanentProperty {
	ID,
	NAME,
	RULE_DEFINITIONS,
	RULE,
	RULESET;
    }
    public enum Child implements AddRemoveProperty {
	KNOWN_RULESETS;
    }

    public static interface Ruleset extends ScoreBoardEventProvider {
        public Map<Rule, String> getAll();
        public String get(Rule k);

        public String getId();
        public String getName();
        public void setName(String n);
        public String getParentRulesetId();
        public void setParentRulesetId(String id);

        // A missing entry means no override for that rule.
        public void setAll(Map<Rule, String> s);

        public enum Value implements PermanentProperty {
            ID,
            PARENT_ID,
            NAME;
        }
        public enum Child implements AddRemoveProperty {
            RULE;
        }
    }
}
