/*
 * Lint
 * Copyright (C) 2020 hYdos, Valoeghese, ramidzkh
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.hydos.lint.block.entity;

import me.hydos.lint.Lint;
import me.hydos.lint.block.SmelteryBlock;
import me.hydos.lint.fluid.LintFluids;
import me.hydos.lint.fluid.MoltenMetalFluid;
import me.hydos.lint.screenhandler.SmelteryScreenHandler;
import me.hydos.lint.tag.LintBlockTags;
import me.hydos.lint.util.LintInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmelteryBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

	public BlockPos center;
	private boolean basicCasting;
	private boolean validMultiblock;
	public LintInventory inventory = new LintInventory(9);
	public List<FluidState> fluidData = new ArrayList<>();

	public SmelteryBlockEntity() {
		super(BlockEntities.SMELTERY);
		Direction facingDirection = world.getBlockState(getPos()).get(SmelteryBlock.FACING);
		center = pos.offset(facingDirection.getOpposite());
	}

	@Override
	public Text getDisplayName() {
		return new LiteralText("Basic Smeltery");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		updateMultiblock(world.getBlockState(pos));
		return new SmelteryScreenHandler(syncId, inv, this.inventory);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putBoolean("basic_casting", basicCasting);
		tag.putBoolean("valid_multiblock", validMultiblock);
		Inventories.toTag(tag, inventory.getRawList());
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		//FIXME: for testing
		fluidData.add(LintFluids.MOLTEN_FLUID_MAP.get(Lint.id("gold")).state().with(MoltenMetalFluid.LEVEL, 2));

		basicCasting = tag.getBoolean("basic_casting");
		validMultiblock = tag.getBoolean("valid_multiblock");
		inventory = new LintInventory(9);
		Inventories.fromTag(tag, inventory.getRawList());
		super.fromTag(state, tag);
	}

	public <T extends Comparable<T>> void setBlockstateProperty(Property<T> property, T value) {
		if (world.getBlockState(pos).get(property) != value) {
			world.setBlockState(pos, world.getBlockState(pos).with(property, value));
		}
	}

	public boolean isLit() {
		return world.getBlockState(pos).get(SmelteryBlock.LIT);
	}

	public void updateMultiblock(BlockState state) {
		if (!state.get(SmelteryBlock.LIT)) {
			if (!world.isClient()) {
				Direction facingDirection = state.get(SmelteryBlock.FACING);
				center = pos.offset(facingDirection.getOpposite());
				BlockPos topCenter = center.up(1);

				int validDirections = 0;
				// loop through all but up and down directions and check for the casting tag
				for (Direction direction : Direction.values()) {
					if (direction != Direction.DOWN && direction != Direction.UP) {
						Block lowerWall = world.getBlockState(center.offset(direction)).getBlock();
						Block upperWall = world.getBlockState(topCenter.offset(direction)).getBlock();
						if (lowerWall.isIn(LintBlockTags.BASIC_CASING) && upperWall.isIn(LintBlockTags.BASIC_CASING)) {
							validDirections++;
						}
					}
				}
				if (validDirections == 4) {
					System.out.println("Valid multiblock!");
					this.validMultiblock = true;
					this.basicCasting = true;
					setBlockstateProperty(SmelteryBlock.LIT, true);
				} else {
					setBlockstateProperty(SmelteryBlock.LIT, false);
					this.validMultiblock = false;
				}
			}
		}
	}
}
