package com.farmrunhelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PatchStateDecoderParityTest
{
	@Test
	public void matchesRuneLiteTimeTrackingForAllWoodyPatchValues() throws ReflectiveOperationException
	{
		assertParity(PatchType.TREE, PatchImplementation.TREE);
		assertParity(PatchType.FRUIT_TREE, PatchImplementation.FRUIT_TREE);
		assertParity(PatchType.HARDWOOD_TREE, PatchImplementation.HARDWOOD_TREE);
		assertParity(PatchType.CALQUAT, PatchImplementation.CALQUAT);
	}

	private static void assertParity(PatchType type, PatchImplementation implementation)
		throws ReflectiveOperationException
	{
		for (int value = 0; value <= 255; value++)
		{
			Snapshot expected = decodeRuneLite(implementation, value);
			DecodedPatchState actual = PatchStateDecoder.decode(type, value);
			String message = type + " varbit value " + value;
			if (expected == null)
			{
				assertNull(message, actual);
				continue;
			}

			assertNotNull(message, actual);
			assertEquals(message, expected.getCropName(), actual.getCropName());
			assertEquals(message, expected.getState(), actual.getState().name());
			if (actual.getState() == PatchState.GROWING)
			{
				assertEquals(message, expected.getStage(), actual.getStage());
				assertEquals(message, expected.getStages(), actual.getStages());
				assertEquals(message, expected.getTickRate(), actual.getTickRateMinutes());
			}
		}
	}

	private static Snapshot decodeRuneLite(PatchImplementation implementation, int value)
		throws ReflectiveOperationException
	{
		Method decode = PatchImplementation.class.getDeclaredMethod("forVarbitValue", int.class);
		decode.setAccessible(true);
		Object state;
		try
		{
			state = decode.invoke(implementation, value);
		}
		catch (InvocationTargetException exception)
		{
			throw new AssertionError(exception.getCause());
		}
		if (state == null)
		{
			return null;
		}

		Object produce = invoke(state, "getProduce");
		Object cropState = invoke(state, "getCropState");
		int stage = (int) invoke(state, "getStage");
		int stages = (int) invoke(state, "getStages");
		int tickRate = (int) invoke(state, "getTickRate");
		String cropName = (String) invoke(produce, "getName");
		String upstreamState = cropState.toString();

		String normalizedState;
		if ("WEEDS".equals(produce.toString()))
		{
			cropName = "Empty";
			normalizedState = "EMPTY";
		}
		else if ("HARVESTABLE".equals(upstreamState)
			|| ("GROWING".equals(upstreamState) && stage == stages - 1))
		{
			normalizedState = "READY";
		}
		else
		{
			normalizedState = upstreamState;
		}

		return new Snapshot(cropName, normalizedState, stage, stages, tickRate);
	}

	private static Object invoke(Object target, String methodName) throws ReflectiveOperationException
	{
		Method method = target.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		return method.invoke(target);
	}

	private static final class Snapshot
	{
		private final String cropName;
		private final String state;
		private final int stage;
		private final int stages;
		private final int tickRate;

		private Snapshot(String cropName, String state, int stage, int stages, int tickRate)
		{
			this.cropName = cropName;
			this.state = state;
			this.stage = stage;
			this.stages = stages;
			this.tickRate = tickRate;
		}

		private String getCropName()
		{
			return cropName;
		}

		private String getState()
		{
			return state;
		}

		private int getStage()
		{
			return stage;
		}

		private int getStages()
		{
			return stages;
		}

		private int getTickRate()
		{
			return tickRate;
		}
	}
}
