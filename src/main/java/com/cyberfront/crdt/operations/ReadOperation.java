/*
 * Copyright (c) 2017 Cybernetic Frontiers LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package com.cyberfront.crdt.operations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The ReadOperation encapsulates the reading a JSON document from the CRDT.  It should have
 * the highest timestamp later than the CRDT CreateOperation and earlier than any DelteOperation
 * CRDT operation  An RFC 6902 formated JSON object isn't used here, instead the operation has no
 * effect on the JsonDocument being reconstructed.
 */
public class ReadOperation extends AbstractOperation {
	
	/** Logger to use when displaying state information */
	@SuppressWarnings("unused")
	private Logger logger = LogManager.getLogger(ReadOperation.class);
	
	/**
	 * This instantiates a new ReadOperation given a timestamp. 
	 *
	 * @param timeStamp The effective time stamp of the operation 
	 */
	public ReadOperation(Long timeStamp) {
		super(null, timeStamp);
	}
	
	/**
	 * Instantiates a copy of the given ReadOperation
	 *
	 * @param src The source ReadOperation to copy
	 */
	public ReadOperation(ReadOperation src) {
		super(src);
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#processOperation(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public JsonNode processOperation(JsonNode document) {
		return document;
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj instanceof ReadOperation && super.equals(obj));
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#getType()
	 */
	@Override
	public OperationType getType() {
		return OperationType.READ;
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#copy()
	 */
	@Override
	public AbstractOperation copy() {
		return new ReadOperation(this);
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#copy()
	 */
	@Override
	public AbstractOperation mimic() {
		return new ReadOperation(this.getTimeStamp());
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#isCreated()
	 */
	@Override
	public boolean isCreated() { return false; }

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.operations.AbstractOperation#isDeleted()
	 */
	@Override
	public boolean isDeleted() { return false; }
}
