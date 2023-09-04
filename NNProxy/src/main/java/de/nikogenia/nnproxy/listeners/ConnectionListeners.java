package de.nikogenia.nnproxy.listeners;

import de.nikogenia.nnproxy.Main;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListeners implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {

        ServerPing ping = event.getResponse();

        ping.setDescriptionComponent(new TextComponent(Main.getInstance().getMotd()));

        ServerPing.Players players = ping.getPlayers();
        players.setMax(1000);
        players.setOnline(42);
        players.setSample(new ServerPing.PlayerInfo[]{});
        ping.setPlayers(players);

        event.setResponse(ping);

    }

}
