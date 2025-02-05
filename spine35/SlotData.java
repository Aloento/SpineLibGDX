/******************************************************************************
 * Spine Runtimes Software License v2.5
 *
 * Copyright (c) 2013-2016, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable, and
 * non-transferable license to use, install, execute, and perform the Spine
 * Runtimes software and derivative works solely for personal or internal
 * use. Without the written permission of Esoteric Software (see Section 2 of
 * the Spine Software License Agreement), you may not (a) modify, translate,
 * adapt, or develop new applications using the Spine Runtimes or otherwise
 * create derivative works or improvements of the Spine Runtimes or (b) remove,
 * delete, alter, or obscure any trademarks or any copyright, trademark, patent,
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, BUSINESS INTERRUPTION, OR LOSS OF
 * USE, DATA, OR PROFITS) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine35;

import com.badlogic.gdx.graphics.Color;

/** Stores the setup pose for a {@link Slot}. */
public class SlotData {
	final int index;
	final String name;
	final BoneData boneData;
	final Color color = new Color(1, 1, 1, 1);
	String attachmentName;
	BlendMode blendMode;

	public SlotData (int index, String name, BoneData boneData) {
		if (index < 0) throw new IllegalArgumentException("index must be >= 0.");
		if (name == null) throw new IllegalArgumentException("name cannot be null.");
		if (boneData == null) throw new IllegalArgumentException("boneData cannot be null.");
		this.index = index;
		this.name = name;
		this.boneData = boneData;
	}

	/** The index of the slot in {@link Skeleton#getSlots()}. */
	public int getIndex () {
		return index;
	}

	/** The name of the slot, which is unique within the skeleton. */
	public String getName () {
		return name;
	}

	/** The bone this slot belongs to. */
	public BoneData getBoneData () {
		return boneData;
	}

	/** The color used to tint the slot's attachment. */
	public Color getColor () {
		return color;
	}

	/** @param attachmentName May be null. */
	public void setAttachmentName (String attachmentName) {
		this.attachmentName = attachmentName;
	}

	/** The name of the attachment that is visible for this slot in the setup pose, or null if no attachment is visible. */
	public String getAttachmentName () {
		return attachmentName;
	}

	/** The blend mode for drawing the slot's attachment. */
	public BlendMode getBlendMode () {
		return blendMode;
	}

	public void setBlendMode (BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public String toString () {
		return name;
	}
}
