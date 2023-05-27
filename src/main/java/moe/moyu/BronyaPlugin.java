package moe.moyu;

import moe.moyu.manager.FriendEventManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public final class BronyaPlugin extends JavaPlugin {
    public static final BronyaPlugin INSTANCE = new BronyaPlugin();

    @Override
    public void onEnable() {
        getLogger().info("Bronya Loaded!");
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, FriendEventManager::process);
    }

    private BronyaPlugin() {
        super(new JvmPluginDescriptionBuilder("moe.moyu.bronya", "0.1.0")
                .name("bronya plugin")
                .author("moyu")
                .build());
    }
}