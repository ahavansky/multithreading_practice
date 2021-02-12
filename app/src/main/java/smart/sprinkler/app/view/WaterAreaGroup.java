package smart.sprinkler.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import smart.sprinkler.app.R;
import smart.sprinkler.app.WaterControlFragment;

public class WaterAreaGroup extends LinearLayout {
    private static int SELECTED_TEXT_COLOR;
    private static int DEFAULT_TEXT_COLOR;

    private final ArrayList<CheckBox> mItemScheduler = new ArrayList<>(WaterControlFragment
            .WATER_AREA_NUMBER);
    private final ArrayList<TextView> mItemLabel = new ArrayList<>(WaterControlFragment
            .WATER_AREA_NUMBER);

    public WaterAreaGroup(Context context) {
        super(context);
        SELECTED_TEXT_COLOR = context.getResources().getColor(R.color.selected_waterarea_text);
        DEFAULT_TEXT_COLOR = context.getResources().getColor(R.color.default_text);
        addChildren();
    }

    public WaterAreaGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        SELECTED_TEXT_COLOR = context.getResources().getColor(R.color.selected_waterarea_text);
        DEFAULT_TEXT_COLOR = context.getResources().getColor(R.color.default_text);
        addChildren();
    }

    public WaterAreaGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        SELECTED_TEXT_COLOR = context.getResources().getColor(R.color.selected_waterarea_text);
        DEFAULT_TEXT_COLOR = context.getResources().getColor(R.color.default_text);
        addChildren();
    }

    private void addChildren() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClickable(false);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1.0f;

        for (int i = 0; i < WaterControlFragment.WATER_AREA_NUMBER; i++) {
            LinearLayout childLayout = (LinearLayout) inflate(getContext(),
                    R.layout.item_water_area, null);
            childLayout.setLayoutParams(lp);

            mItemScheduler.add((CheckBox) childLayout.findViewById(R.id.water_area_scheduler));
            mItemLabel.add((TextView) childLayout.findViewById(R.id.water_area_label));
            addView(childLayout);
        }
    }

    public void setLabels(String[] labels) {
        if (labels != null) {
            int j = 0;
            for (TextView tView : mItemLabel) {
                tView.setText(labels[j]);
                mItemScheduler.get(j).setContentDescription(labels[j]);
                j++;
            }
        }
    }

    public void setWateringIndicator(int index, boolean isWatering) {
        LinearLayout wateringArea = (LinearLayout) getChildAt(index);
        TextView areaName = wateringArea.findViewById(R.id.water_area_label);
        areaName.setTextColor(isWatering ? SELECTED_TEXT_COLOR : DEFAULT_TEXT_COLOR);
        CheckBox areaIndicator = wateringArea.findViewById(R.id.water_area_indicator);
        areaIndicator.setChecked(isWatering);
    }

    public void setWateringScheduler(int index, boolean state) {
        mItemScheduler.get(index).setChecked(state);
    }

    public boolean[] waterSchedulerState() {
        boolean[] res = new boolean[WaterControlFragment.WATER_AREA_NUMBER];
        int i = 0;
        for (CheckBox cBox : mItemScheduler) {
            res[i] = cBox.isChecked();
            i++;
        }
        return res;
    }
}
