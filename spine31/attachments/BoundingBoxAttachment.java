/******************************************************************************
 * Spine Runtimes Software License
 * Version 2.3
 * 
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 * 
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 * 
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine31.attachments;

import com.esotericsoftware.spine31.Bone;
import com.esotericsoftware.spine31.Skeleton;

public class BoundingBoxAttachment extends Attachment {
	private float[] vertices;

	public BoundingBoxAttachment (String name) {
		super(name);
	}

	public void computeWorldVertices (Bone bone, float[] worldVertices) {
		Skeleton skeleton = bone.getSkeleton();
		float x = skeleton.getX() + bone.getWorldX(), y = skeleton.getY() + bone.getWorldY();
		float m00 = bone.getA();
		float m01 = bone.getB();
		float m10 = bone.getC();
		float m11 = bone.getD();
		float[] vertices = this.vertices;
		for (int i = 0, n = vertices.length; i < n; i += 2) {
			float px = vertices[i];
			float py = vertices[i + 1];
			worldVertices[i] = px * m00 + py * m01 + x;
			worldVertices[i + 1] = px * m10 + py * m11 + y;
		}
	}

	public float[] getVertices () {
		return vertices;
	}

	public void setVertices (float[] vertices) {
		this.vertices = vertices;
	}
}
