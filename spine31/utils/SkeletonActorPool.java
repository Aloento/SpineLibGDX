
package com.esotericsoftware.spine31.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine31.AnimationState;
import com.esotericsoftware.spine31.AnimationState.TrackEntry;
import com.esotericsoftware.spine31.AnimationStateData;
import com.esotericsoftware.spine31.Skeleton;
import com.esotericsoftware.spine31.SkeletonData;
import com.esotericsoftware.spine31.SkeletonRenderer;
import com.esotericsoftware.spine31.Skin;

public class SkeletonActorPool extends Pool<SkeletonActor> {
	private SkeletonRenderer renderer;
	SkeletonData skeletonData;
	AnimationStateData stateData;
	private final Pool<Skeleton> skeletonPool;
	private final Pool<AnimationState> statePool;
	private final Array<SkeletonActor> obtained;

	public SkeletonActorPool (SkeletonRenderer renderer, SkeletonData skeletonData, AnimationStateData stateData) {
		this(renderer, skeletonData, stateData, 16, Integer.MAX_VALUE);
	}

	public SkeletonActorPool (SkeletonRenderer renderer, SkeletonData skeletonData, AnimationStateData stateData,
		int initialCapacity, int max) {
		super(initialCapacity, max);

		this.renderer = renderer;
		this.skeletonData = skeletonData;
		this.stateData = stateData;

		obtained = new Array(false, initialCapacity);

		skeletonPool = new Pool<Skeleton>(initialCapacity, max) {
			protected Skeleton newObject () {
				return new Skeleton(SkeletonActorPool.this.skeletonData);
			}

			protected void reset (Skeleton skeleton) {
				skeleton.setColor(Color.WHITE);
				skeleton.setFlip(false, false);
				skeleton.setSkin((Skin)null);
				skeleton.setSkin(SkeletonActorPool.this.skeletonData.getDefaultSkin());
				skeleton.setToSetupPose();
			}
		};

		statePool = new Pool<AnimationState>(initialCapacity, max) {
			protected AnimationState newObject () {
				return new AnimationState(SkeletonActorPool.this.stateData);
			}

			protected void reset (AnimationState state) {
				state.clearTracks();
				state.clearListeners();
			}
		};
	}

	/** Each obtained skeleton actor that is no longer playing an animation is removed from the stage and returned to the pool. */
	public void freeComplete () {
		Array<SkeletonActor> obtained = this.obtained;
		outer:
		for (int i = obtained.size - 1; i >= 0; i--) {
			SkeletonActor actor = obtained.get(i);
			Array<TrackEntry> tracks = actor.state.getTracks();
			for (int ii = 0, nn = tracks.size; ii < nn; ii++)
				if (tracks.get(ii) != null) continue outer;
			free(actor);
		}
	}

	protected SkeletonActor newObject () {
		SkeletonActor actor = new SkeletonActor();
		actor.setRenderer(renderer);
		return actor;
	}

	/** This pool keeps a reference to the obtained instance, so it should be returned to the pool via {@link #free(SkeletonActor)}
	 * , {@link #freeAll(Array)} or {@link #freeComplete()} to avoid leaking memory. */
	public SkeletonActor obtain () {
		SkeletonActor actor = super.obtain();
		actor.setSkeleton(skeletonPool.obtain());
		actor.setAnimationState(statePool.obtain());
		obtained.add(actor);
		return actor;
	}

	protected void reset (SkeletonActor actor) {
		actor.remove();
		obtained.removeValue(actor, true);
		skeletonPool.free(actor.getSkeleton());
		statePool.free(actor.getAnimationState());
	}

	public Array<SkeletonActor> getObtained () {
		return obtained;
	}
}
