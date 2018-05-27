package grp2.fitness;

import android.view.View;
import android.widget.TextView;

import com.amazonaws.models.nosql.DailyDataDO;

import org.junit.Test;

import java.util.ArrayList;

import grp2.fitness.handlers.LeaderboardAdapter;
import grp2.fitness.handlers.LeaderboardAdapter.DailyDataView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LeaderboardAdapterTest {

    @Test
    public void testOnBindViewHolder_setsTitle() {
        DailyDataView dataView = makeMockDailyDataView();
        String nickname = "nickname";
        ArrayList<DailyDataDO> leaderboard = new ArrayList<>();
        DailyDataDO dataDO = new DailyDataDO();
        dataDO.setNickname(nickname);
        leaderboard.add(dataDO);
        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(leaderboard);

        leaderboardAdapter.onBindViewHolder(dataView, 0);

        verify(dataView.title).setText(nickname);

    }

    private DailyDataView makeMockDailyDataView() {
        View view = mock(View.class);
        DailyDataView dataView = new DailyDataView(view);

        dataView.title = mock(TextView.class);
        doNothing().when(dataView.title).setText(any(String.class));
        dataView.energyTV = mock(TextView.class);
        doNothing().when(dataView.energyTV).setText(any(String.class));
        dataView.heartRateTV = mock(TextView.class);
        doNothing().when(dataView.heartRateTV).setText(any(String.class));
        dataView.stepsTV = mock(TextView.class);
        doNothing().when(dataView.stepsTV).setText(any(String.class));

        return dataView;
    }
}
