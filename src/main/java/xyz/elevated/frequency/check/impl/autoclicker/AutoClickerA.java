package xyz.elevated.frequency.check.impl.autoclicker;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.Pair;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInArmAnimation;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@CheckData(name = "AutoClicker (A)")
public final class AutoClickerA extends PacketCheck {
    private int movements = 0;
    private Deque<Integer> samples = Lists.newLinkedList();

    public AutoClickerA(final PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(final Object object) {
        if (object instanceof WrappedPlayInArmAnimation) {
            final boolean valid = movements < 4 && !playerData.getActionManager().getDigging().get();

            if (valid) samples.add(movements);

            if (samples.size() == 20) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                final double deviation = MathUtil.getStandardDeviation(samples);
                final double outliers = outlierPair.getX().size() + outlierPair.getY().size();

                if (deviation < 2.d && outliers < 2) fail();

                samples.clear();
            }

            movements = 0;
        } else if (object instanceof WrappedPlayInFlying) {
            ++movements;
        }
    }
}