package io.github.haykam821.voidbubbling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

@Mixin(Entity.class)
public class EntityMixin {
	@Unique
	private static BlockState STATE;

	@Unique
	private static boolean hasVoidBubbling(Entity entity) {
		// Prevent spectators from being affected
		if (entity.noClip) return false;

		if (entity.getY() < 0) {
			if (entity instanceof MobEntity && !((MobEntity) entity).isPersistent()) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void attemptVoidBubbling(CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (EntityMixin.hasVoidBubbling(entity)) {
			if (EntityMixin.STATE == null) {
				EntityMixin.STATE = Blocks.BUBBLE_COLUMN.getDefaultState().with(BubbleColumnBlock.DRAG, false);
			}
			EntityMixin.STATE.onEntityCollision(entity.getEntityWorld(), entity.getBlockPos(), entity);
		}
	}
}
