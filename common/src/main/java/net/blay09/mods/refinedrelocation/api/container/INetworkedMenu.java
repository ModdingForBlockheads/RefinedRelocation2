package net.blay09.mods.refinedrelocation.api.container;

public interface INetworkedMenu {
	 void receivedMessageClient(IMenuMessage message);
	 void receivedMessageServer(IMenuMessage message);
}
