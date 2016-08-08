package WorldEdit;

import cn.nukkit.Player;

public class PlayerData {

    private Player player;

	private Selection selection;

    public BlocksCopy copiedBlocks = null;

    public PlayerData(Player p) {
        player = p;
        selection = new Selection();
    }

	public Selection getSelection() {
		return selection;
	}

	public Player getPlayer() {
		return player;
	}
}
