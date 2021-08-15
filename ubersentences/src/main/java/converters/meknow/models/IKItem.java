package converters.meknow.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IKItem {
    @SerializedName("goal_items")
    private List<GoalItem> goalItems = null;

    public List<GoalItem> getGoalItems() {
        return goalItems;
    }

    public void setGoalItems(List<GoalItem> goalItems) {
        this.goalItems = goalItems;
    }

}
