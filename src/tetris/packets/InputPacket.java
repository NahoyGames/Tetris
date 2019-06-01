package tetris.packets;

import tetris.TetrisInput;

public class InputPacket
{
	public byte input;
	public boolean on;


	public InputPacket() { }
	public InputPacket(TetrisInput input, boolean on)
	{
		this.input = (byte)input.ordinal();
		this.on = on;
	}


	public TetrisInput input()
	{
		return TetrisInput.values()[this.input];
	}
}
