package es.usc.citius.servando.calendula.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.SummaryCalendarActivity;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Presentation;
import es.usc.citius.servando.calendula.persistence.Schedule;
import es.usc.citius.servando.calendula.persistence.ScheduleItem;
import es.usc.citius.servando.calendula.scheduling.ScheduleUtils;
import es.usc.citius.servando.calendula.util.ScheduleHelper;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ScheduleSummaryFragment extends Fragment {

    public static final String TAG = ScheduleSummaryFragment.class.getName();

    public ScheduleSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_summary, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // update summary info when this fragment becomes visible
            updateSummary();
        }
    }

    public void updateSummary() {

        Log.d(TAG, "updateSummary ScheduleSUmmaryFragment");
        View rootView = getView();

        Medicine med = ScheduleHelper.instance().getSelectedMed();
        Schedule s = ScheduleHelper.instance().getSchedule();
        List<ScheduleItem> items = ScheduleHelper.instance().getScheduleItems();

        final TextView medNameTv = (TextView) rootView.findViewById(R.id.sched_summary_medname);
        final TextView medDaysTv = (TextView) rootView.findViewById(R.id.sched_summary_medi_days);
        final TextView medDailyFreqTv = (TextView) rootView.findViewById(R.id.sched_summary_medi_dailyfreq);
        final ImageView medIconImage = (ImageView) rootView.findViewById(R.id.sched_summary_medicon);
        final Button showCalendarButton = (Button) rootView.findViewById(R.id.button_show_calendar);

        //String medName = med != null ? med.name() : "Unselected";
        int medIcon = med != null ? med.presentation().getDrawable() : Presentation.PILLS.getDrawable();

        if (med != null) {
            medNameTv.setText(med.name());
        }

        medDaysTv.setText(s.toReadableString(getActivity()));
        medIconImage.setImageDrawable(getResources().getDrawable(medIcon));

        if (s.type() != Schedule.SCHEDULE_TYPE_HOURLY) {
            String freq =
                    ScheduleUtils.getTimesStr(items != null ? items.size() : 0, getActivity());
            medDailyFreqTv.setText(freq);
        } else {
            String freq = ScheduleUtils.getTimesStr(24 / s.rule().interval(), getActivity());
            medDailyFreqTv.setText(freq);
        }

        showCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Schedule s = ScheduleHelper.instance().getSchedule();

                LocalDate start = s.start();

                Intent i = new Intent(getActivity(), SummaryCalendarActivity.class);

                if (start != null) {
                    i.putExtra("start", start.toString(SummaryCalendarActivity.START_DATE_FORMAT));
                }

                if (s.type() == Schedule.SCHEDULE_TYPE_CYCLE) {
                    i.putExtra("active_days", s.getCycleDays());
                    i.putExtra("rest_days", s.getCycleRest());
                } else {
                    i.putExtra("rule", s.rule().toIcal());
                }

                startActivity(i);
            }
        });


    }


}
