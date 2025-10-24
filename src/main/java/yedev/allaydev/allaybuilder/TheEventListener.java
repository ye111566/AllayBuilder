package yedev.allaydev.allaybuilder;

import org.allaymc.api.player.GameMode;
import org.allaymc.api.server.Server;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerInteractBlockEvent;

import java.util.ArrayList;
import java.util.List;

public class TheEventListener {
    public static List<String> cache = new ArrayList<>();

    @EventHandler
    private void onPlayerInteractBlock(PlayerInteractBlockEvent event) {
        if(!event.getPlayer().isOperator()) return;
        String playername=event.getPlayer().getOriginName();
        if(playername.isEmpty()){
            return;
        }
        if(event.getPlayer().getItemInHand().getItemType().getIdentifier().toString().equals("minecraft:wooden_axe")){
            //System.out.println("检测到手持木斧右键");
            if(!cache.contains(playername)){
                AllayBuilder.onwoodenaxe(event.getPlayer(),event.getInteractInfo().clickedBlockPos());
                cache.add(playername);

                Server.getInstance().getScheduler().scheduleDelayed(AllayBuilder.instance,()->{
                    TheEventListener.cache.remove(playername);
                    return true;
                },20);

            }
        }


    }
}
