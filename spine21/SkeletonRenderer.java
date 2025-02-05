/******************************************************************************
 * Spine Runtimes Software License
 * Version 2.1
 * 
 * Copyright (c) 2013, Esoteric Software
 * All rights reserved.
 * 
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to install, execute and perform the Spine Runtimes
 * Software (the "Software") solely for internal use. Without the written
 * permission of Esoteric Software (typically granted by licensing Spine), you
 * may not (a) modify, translate, adapt or otherwise create derivative works,
 * improvements of the Software or develop new applications using the Software
 * or (b) remove, delete, alter or obscure any trademarks or any copyright,
 * trademark, patent or other intellectual property or proprietary rights
 * notices on or in the Software, including any copy thereof. Redistributions
 * in binary or source form must include this license and terms.
 * 
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine21;

import com.esotericsoftware.spine21.attachments.Attachment;
import com.esotericsoftware.spine21.attachments.MeshAttachment;
import com.esotericsoftware.spine21.attachments.RegionAttachment;
import com.esotericsoftware.spine21.attachments.SkeletonAttachment;
import com.esotericsoftware.spine21.attachments.SkinnedMeshAttachment;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.Array;

public class SkeletonRenderer {
	static private final short[] quadTriangles = {0, 1, 2, 2, 3, 0};

	private boolean premultipliedAlpha;

	@SuppressWarnings("null")
	public void draw (PolygonSpriteBatch batch, Skeleton skeleton) {
		boolean premultipliedAlpha = this.premultipliedAlpha;
		int srcFunc = premultipliedAlpha ? GL20.GL_ONE : GL20.GL_SRC_ALPHA;
		batch.setBlendFunction(srcFunc, GL20.GL_ONE_MINUS_SRC_ALPHA);

		boolean additive = false;

		float[] vertices = null;
		short[] triangles = null;
		Array<Slot> drawOrder = skeleton.drawOrder;
		for (int i = 0, n = drawOrder.size; i < n; i++) {
			Slot slot = drawOrder.get(i);
			Attachment attachment = slot.attachment;
			Texture texture = null;
			if (attachment instanceof RegionAttachment) {
				RegionAttachment region = (RegionAttachment)attachment;
				region.updateWorldVertices(slot, premultipliedAlpha);
				vertices = region.getWorldVertices();
				triangles = quadTriangles;
				texture = region.getRegion().getTexture();

			} else if (attachment instanceof MeshAttachment) {
				MeshAttachment mesh = (MeshAttachment)attachment;
				mesh.updateWorldVertices(slot, premultipliedAlpha);
				vertices = mesh.getWorldVertices();
				triangles = mesh.getTriangles();
				texture = mesh.getRegion().getTexture();

			} else if (attachment instanceof SkinnedMeshAttachment) {
				SkinnedMeshAttachment mesh = (SkinnedMeshAttachment)attachment;
				mesh.updateWorldVertices(slot, premultipliedAlpha);
				vertices = mesh.getWorldVertices();
				triangles = mesh.getTriangles();
				texture = mesh.getRegion().getTexture();

			} else if (attachment instanceof SkeletonAttachment) {
				Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
				if (attachmentSkeleton == null) continue;
				Bone bone = slot.getBone();
				Bone rootBone = attachmentSkeleton.getRootBone();
				float oldScaleX = rootBone.getScaleX();
				float oldScaleY = rootBone.getScaleY();
				float oldRotation = rootBone.getRotation();
				attachmentSkeleton.setPosition(skeleton.getX() + bone.getWorldX(), skeleton.getY() + bone.getWorldY());
				rootBone.setScaleX(1 + bone.getWorldScaleX() - oldScaleX);
				rootBone.setScaleY(1 + bone.getWorldScaleY() - oldScaleY);
				rootBone.setRotation(oldRotation + bone.getWorldRotation());
				attachmentSkeleton.updateWorldTransform();

				draw(batch, attachmentSkeleton);

				attachmentSkeleton.setPosition(0, 0);
				rootBone.setScaleX(oldScaleX);
				rootBone.setScaleY(oldScaleY);
				rootBone.setRotation(oldRotation);
			}

			if (texture != null) {
				if (slot.data.getAdditiveBlending() != additive) {
					additive = !additive;
					if (additive)
						batch.setBlendFunction(srcFunc, GL20.GL_ONE);
					else
						batch.setBlendFunction(srcFunc, GL20.GL_ONE_MINUS_SRC_ALPHA);
				}
				batch.draw(texture, vertices, 0, vertices.length, triangles, 0, triangles.length);
			}
		}
	}

	public void draw (Batch batch, Skeleton skeleton) {
		boolean premultipliedAlpha = this.premultipliedAlpha;
		int srcFunc = premultipliedAlpha ? GL20.GL_ONE : GL20.GL_SRC_ALPHA;
		batch.setBlendFunction(srcFunc, GL20.GL_ONE_MINUS_SRC_ALPHA);

		boolean additive = false;

		Array<Slot> drawOrder = skeleton.drawOrder;
		for (int i = 0, n = drawOrder.size; i < n; i++) {
			Slot slot = drawOrder.get(i);
			Attachment attachment = slot.attachment;
			if (attachment instanceof RegionAttachment) {
				RegionAttachment regionAttachment = (RegionAttachment)attachment;
				regionAttachment.updateWorldVertices(slot, premultipliedAlpha);
				float[] vertices = regionAttachment.getWorldVertices();
				if (slot.data.getAdditiveBlending() != additive) {
					additive = !additive;
					if (additive)
						batch.setBlendFunction(srcFunc, GL20.GL_ONE);
					else
						batch.setBlendFunction(srcFunc, GL20.GL_ONE_MINUS_SRC_ALPHA);
				}
				batch.draw(regionAttachment.getRegion().getTexture(), vertices, 0, 20);

			} else if (attachment instanceof MeshAttachment || attachment instanceof SkinnedMeshAttachment) {
				throw new RuntimeException("PolygonSpriteBatch is required to render meshes.");

			} else if (attachment instanceof SkeletonAttachment) {
				Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
				if (attachmentSkeleton == null) continue;
				Bone bone = slot.getBone();
				Bone rootBone = attachmentSkeleton.getRootBone();
				float oldScaleX = rootBone.getScaleX();
				float oldScaleY = rootBone.getScaleY();
				float oldRotation = rootBone.getRotation();
				attachmentSkeleton.setPosition(skeleton.getX() + bone.getWorldX(), skeleton.getY() + bone.getWorldY());
				rootBone.setScaleX(1 + bone.getWorldScaleX() - oldScaleX);
				rootBone.setScaleY(1 + bone.getWorldScaleY() - oldScaleY);
				rootBone.setRotation(oldRotation + bone.getWorldRotation());
				attachmentSkeleton.updateWorldTransform();

				draw(batch, attachmentSkeleton);

				attachmentSkeleton.setX(0);
				attachmentSkeleton.setY(0);
				rootBone.setScaleX(oldScaleX);
				rootBone.setScaleY(oldScaleY);
				rootBone.setRotation(oldRotation);
			}
		}
	}

	public void setPremultipliedAlpha (boolean premultipliedAlpha) {
		this.premultipliedAlpha = premultipliedAlpha;
	}
}
