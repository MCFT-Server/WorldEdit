package WorldEdit;

import cn.nukkit.Player;
import lombok.Getter;

public class PlayerData {

    @Getter
    private Player player;

    @Getter
    private Selection selection;

    @Getter
    public BlocksCopy copiedBlocks = null;

    public PlayerData(Player p) {
        this.player = p;
        selection = new Selection();
    }
}
