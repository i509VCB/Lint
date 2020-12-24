package io.github.hydos.lint.entity;

import io.github.hydos.lint.Lint;
import io.github.hydos.lint.entity.aggressive.GhostEntity;
import io.github.hydos.lint.entity.passive.BeeTaterEntity;
import io.github.hydos.lint.entity.aggressive.I509VCBEntity;
import io.github.hydos.lint.entity.aggressive.KingTaterEntity;
import io.github.hydos.lint.entity.aggressive.TaterMinionEntity;
import io.github.hydos.lint.entity.passive.TinyPotatoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public interface Entities {

	/**
	 * Passive entities
	 */
	EntityType<BeeTaterEntity> BEE_TATER =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("bee_tater"), FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, BeeTaterEntity::new)
					.dimensions(EntityDimensions.fixed(0.3f, 0.4f))
					.build());
	EntityType<TinyPotatoEntity> TINY_POTATO =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("tiny_potato"), FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, TinyPotatoEntity::new)
					.dimensions(EntityDimensions.fixed(0.3f, 0.4f))
					.build());

	/**
	 * Aggressive entities
	 */
	EntityType<GhostEntity> GHOST =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("ghost"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GhostEntity::new)
			.dimensions(EntityDimensions.fixed(1, 2f))
			.build());

	/**
	 * Bosses
	 */
	EntityType<TaterMinionEntity> MINION =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("minion"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, (EntityType<TaterMinionEntity> type, World world) -> new TaterMinionEntity(type, world, null))
					.dimensions(TINY_POTATO.getDimensions())
					.build());

	EntityType<KingTaterEntity> KING_TATER =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("king_tater"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KingTaterEntity::new)
					.dimensions(EntityDimensions.changing(2f, 2f))
					.build());

	EntityType<I509VCBEntity> I5 =
			Registry.register(Registry.ENTITY_TYPE, Lint.id("i509vcb"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, I509VCBEntity::new)
					.dimensions(EntityDimensions.changing(1f, 4f))
					.build());

	static void initialize() {
		FabricDefaultAttributeRegistry.register(Entities.TINY_POTATO, TinyPotatoEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(Entities.BEE_TATER, TinyPotatoEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(Entities.MINION, TaterMinionEntity.createAttributes());

		FabricDefaultAttributeRegistry.register(Entities.KING_TATER, KingTaterEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(Entities.I5, I509VCBEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(Entities.GHOST, GhostEntity.createHostileAttributes());

		SpawnRestriction.register(Entities.TINY_POTATO, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TinyPotatoEntity::canSpawn);
	}
}
