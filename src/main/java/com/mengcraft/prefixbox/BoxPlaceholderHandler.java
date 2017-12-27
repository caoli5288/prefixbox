package com.mengcraft.prefixbox;

import com.google.common.collect.ImmutableMap;
import com.mengcraft.prefixbox.util.MyList;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Function;

public class BoxPlaceholderHandler extends EZPlaceholderHook {

    private final Map<String, Function<Player, String>> all;

    public BoxPlaceholderHandler(Main main) {
        super(main, "pbox");
        all = ImmutableMap.of("all", who -> main.getExecutor().getAll().size() + "",
                "my", who -> {
                    MyList list = PrefixManager.INSTANCE.list(who);
                    if (list == null) return "NaN";
                    return list.size() + "";
                }
        );
        main.getLogger().info("Placeholder initial -> " + all);
    }

    @Override
    public String onPlaceholderRequest(Player who, String label) {
        Function<Player, String> func = all.get(label);
        if (func == null) return "NaN";

        return func.apply(who);
    }
}
