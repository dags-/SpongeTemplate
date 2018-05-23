package me.dags.spongetemplate;

import com.google.inject.Inject;
import java.nio.file.Path;
import me.dags.commandbus.CommandBus;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.Join;
import me.dags.commandbus.annotation.Src;
import me.dags.commandbus.fmt.Fmt;
import me.dags.config.Config;
import me.dags.textmu.MarkupSpec;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "plugin", name = "Plugin", version = "0.1", description = "A plugin")
public class SpongePlugin {

    private final Config config;

    @Inject
    public SpongePlugin(@DefaultConfig(sharedRoot = false) Path config) {
        this.config = Config.must(config);
    }

    @Listener
    public void onPreInit(GameInitializationEvent e) {
        CommandBus.create(this).register(this).submit();
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join e) {
        String format = config.get("join", "[green]({name}) [yellow](joined the server!)");
        Text text = MarkupSpec.create().template(format).with("name", e.getTargetEntity().getName()).render();
        Sponge.getServer().getBroadcastChannel().send(text);
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect e) {
        String format = config.get("quit", "[dark_gray]({name}) [gray](left the server!)");
        Text text = MarkupSpec.create().template(format).with("name", e.getTargetEntity().getName()).render();
        Sponge.getServer().getBroadcastChannel().send(text);
    }

    @Command("format join <message>")
    public void setJoin(@Src CommandSource source, @Join String format) {
        config.set("join", format);
        config.save();
        Text text = MarkupSpec.create().template(format).with("name", source.getName()).render();
        Fmt.stress("Join Preview: ").append(text).tell(source);
    }

    @Command("format quit <message>")
    public void setQuit(@Src CommandSource source, @Join String format) {
        config.set("quit", format);
        config.save();
        Text text = MarkupSpec.create().template(format).with("name", source.getName()).render();
        Fmt.stress("Quit Preview: ").append(text).tell(source);
    }
}
