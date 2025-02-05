/******************************************************************************
 * Spine Runtimes License Agreement
 * Last updated May 1, 2019. Replaces all prior versions.
 *
 * Copyright (c) 2013-2019, Esoteric Software LLC
 *
 * Integration of the Spine Runtimes into software or otherwise creating
 * derivative works of the Spine Runtimes is permitted under the terms and
 * conditions of Section 2 of the Spine Editor License Agreement:
 * http://esotericsoftware.com/spine-editor-license
 *
 * Otherwise, it is permitted to integrate the Spine Runtimes into software
 * or otherwise create derivative works of the Spine Runtimes (collectively,
 * "Products"), provided that each user of the Products must obtain their own
 * Spine Editor license and redistribution of the Products in any form must
 * include this license and copyright notice.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE LLC "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL ESOTERIC SOFTWARE LLC BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, BUSINESS
 * INTERRUPTION, OR LOSS OF USE, DATA, OR PROFITS) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine37;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;

import com.esotericsoftware.spine37.attachments.Attachment;

/** Stores attachments by slot index and attachment name.
 * <p>
 * See SkeletonData {@link SkeletonData#defaultSkin}, Skeleton {@link Skeleton#skin}, and
 * <a href="http://esotericsoftware.com/spine-runtime-skins">Runtime skins</a> in the Spine Runtimes Guide. */
public class Skin {
	final String name;
	final ObjectMap<Key, Attachment> attachments = new ObjectMap();
	private final Key lookup = new Key();
	final Pool<Key> keyPool = new Pool(64) {
		protected Object newObject () {
			return new Key();
		}
	};

	public Skin (String name) {
		if (name == null) throw new IllegalArgumentException("name cannot be null.");
		this.name = name;
	}

	/** Adds an attachment to the skin for the specified slot index and name. */
	public void addAttachment (int slotIndex, String name, Attachment attachment) {
		if (attachment == null) throw new IllegalArgumentException("attachment cannot be null.");
		if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0.");
		Key key = keyPool.obtain();
		key.set(slotIndex, name);
		attachments.put(key, attachment);
	}

	/** Adds all attachments from the specified skin to this skin. */
	public void addAttachments (Skin skin) {
		for (Entry<Key, Attachment> entry : skin.attachments.entries())
			addAttachment(entry.key.slotIndex, entry.key.name, entry.value);
	}

	/** Returns the attachment for the specified slot index and name, or null. */
	public Attachment getAttachment (int slotIndex, String name) {
		if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0.");
		lookup.set(slotIndex, name);
		return attachments.get(lookup);
	}

	/** Removes the attachment in the skin for the specified slot index and name, if any. */
	public void removeAttachment (int slotIndex, String name) {
		if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0.");
		Key key = keyPool.obtain();
		key.set(slotIndex, name);
		attachments.remove(key);
		keyPool.free(key);
	}

	public void findNamesForSlot (int slotIndex, Array<String> names) {
		if (names == null) throw new IllegalArgumentException("names cannot be null.");
		if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0.");
		for (Key key : attachments.keys())
			if (key.slotIndex == slotIndex) names.add(key.name);
	}

	public void findAttachmentsForSlot (int slotIndex, Array<Attachment> attachments) {
		if (attachments == null) throw new IllegalArgumentException("attachments cannot be null.");
		if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0.");
		for (Entry<Key, Attachment> entry : this.attachments.entries())
			if (entry.key.slotIndex == slotIndex) attachments.add(entry.value);
	}

	public void getAttachments (Array<Attachment> attachments) {
		if (attachments == null) throw new IllegalArgumentException("attachments cannot be null.");
		for (Attachment attachment : this.attachments.values())
			attachments.add(attachment);
	}

	public void clear () {
		for (Key key : attachments.keys())
			keyPool.free(key);
		attachments.clear(1024);
	}

	public int size () {
		return attachments.size;
	}

	/** The skin's name, which is unique within the skeleton. */
	public String getName () {
		return name;
	}

	public String toString () {
		return name;
	}

	/** Attach each attachment in this skin if the corresponding attachment in the old skin is currently attached. */
	void attachAll (Skeleton skeleton, Skin oldSkin) {
		for (Entry<Key, Attachment> entry : oldSkin.attachments.entries()) {
			int slotIndex = entry.key.slotIndex;
			Slot slot = skeleton.slots.get(slotIndex);
			if (slot.attachment == entry.value) {
				Attachment attachment = getAttachment(slotIndex, entry.key.name);
				if (attachment != null) slot.setAttachment(attachment);
			}
		}
	}

	static class Key {
		int slotIndex;
		String name;
		int hashCode;

		public void set (int slotIndex, String name) {
			if (name == null) throw new IllegalArgumentException("name cannot be null.");
			this.slotIndex = slotIndex;
			this.name = name;
			hashCode = name.hashCode() + slotIndex * 37;
		}

		public int hashCode () {
			return hashCode;
		}

		public boolean equals (Object object) {
			if (object == null) return false;
			Key other = (Key)object;
			if (slotIndex != other.slotIndex) return false;
			if (!name.equals(other.name)) return false;
			return true;
		}

		public String toString () {
			return slotIndex + ":" + name;
		}
	}
}
