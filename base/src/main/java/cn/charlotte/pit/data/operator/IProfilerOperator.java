package cn.charlotte.pit.data.operator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

public interface IProfilerOperator {
    IOperator getIOperator(UUID uuid);

    IOperator getIOperator(String uuid);

    boolean doSaveProfiles();

    IOperator getOrConstructIOperator(Player target);

    @NotNull IOperator namedIOperator(@NotNull String target);

    @NotNull IOperator lookupIOnline(@NotNull String name);

    @NotNull Object ifPresentAndLoaded(@NotNull Player target, @NotNull Function<IOperator, Unit> function);
}
