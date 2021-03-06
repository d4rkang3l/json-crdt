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
package com.cyberfront.crdt;

import java.util.Collection;
import java.util.TreeSet;

import com.cyberfront.crdt.operations.AbstractOperation;
import com.cyberfront.crdt.operations.AbstractOperation.OperationType;
import com.cyberfront.crdt.support.Support;

/**
 * This is an abstract class which defines a Two Set CRDT.  One set contains operations to use, called an ADD set, and the other contains
 * a set of operations which must not be used, called a REMOVE set.  Elements in the REMOVE set have precedence over those in the ADD set.
 * That is, if an element is contained in the REMOVE set it will not be used if it is in the ADD set, regardless of when it was added to the
 * ADD set.
 */
public abstract class OperationTwoSet extends AbstractCRDT {
	
	/** The ADD set. */
	private Collection<AbstractOperation> addSet;

	/** The REMOVE set. */
	private Collection<AbstractOperation> remSet;
	
	/**
	 * This method retrieved the ADD set.
	 *
	 * @return the ADD set
	 */
	private Collection<AbstractOperation> getAddSet() {
		if (null == this.addSet) {
			this.addSet = new TreeSet<>();
		}
		
		return this.addSet;
	}

	/**
	 * This method retrieved the REMOVE set.
	 *
	 * @return the REMOVE set
	 */
	private Collection<AbstractOperation> getRemSet() {
		if (null == this.remSet) {
			this.remSet = new TreeSet<>();
		}
		return this.remSet;
	}
	
	public Collection<AbstractOperation> copyAddSet() {
		return AbstractOperation.copy(this.getAddSet());
	}

	public Collection<AbstractOperation> copyRemSet() {
		return AbstractOperation.copy(this.getRemSet());
	}

	/**
	 * Retrieve the number of elements in the Add Set 
	 * 
	 * @return The number of elements in the Add Set
	 */
	public long getAddCount() {
		return this.getAddSet().size();
	}

	/**
	 * Retrieve the number of elements in the Remove Set 
	 * 
	 * @return The number of elements in the Remove Set
	 */
	public long getRemCount() {
		return this.getRemSet().size();
	}
	
	/**
	 * Retrieve the number of elements in the final operation set 
	 * 
	 * @return The number of elements in the Remove Set
	 */
	public long getOperationCount() {
		return this.getOpsSet().size();
	}
	
	/**
	 * Insert an operation to the ADD set 
	 *
	 * @param op The operation to add to the ADD set
	 */
	protected void addOperation(AbstractOperation op) {
		this.getAddSet().add(op);
	}
	
	/**
	 * Insert an operation to the REMOVE set 
	 *
	 * @param op The operation to add to the REMOVE set
	 */
	protected void remOperation(AbstractOperation op) {
		this.getRemSet().add(op);
	}
	
	/**
	 * This private static function returns a set resulting from removing all of the elements on the RHS from the set on the LHS
	 *
	 * @param lhs The left hand side of the difference operator
	 * @param rhs The right hand side of the difference operator
	 * @return The set of elements resulting from removing all of the elements in RHS from LHS
	 */
	private static Collection<AbstractOperation> diff(Collection<AbstractOperation> lhs, Collection<AbstractOperation> rhs) {
		Collection<AbstractOperation> rv = new TreeSet<>();
		rv.addAll(lhs);
		rv.removeAll(rhs);
		return rv;
	}
	
	/**
	 * This method returns the collection of elements in the ADD set after those in the REMOVE set have been
	 * removed.
	 *
	 * @return The operations which are active in this Two Set CRDT
	 */
	public Collection<AbstractOperation> getOpsSet() {
		return diff(this.getAddSet(), this.getRemSet());
	}

	/**
	 * This method removes all elements in both the ADD and REMOVE sets, effectively reseting them to empty.
	 */
	public void clear() {
		this.getAddSet().clear();
		this.getRemSet().clear();
	}

	/**
	 * This method determines the state of the CRDT.  If both the ADD and REMOVE sets are empty, then this is empty.  Alternatively if the ADD
	 * set is s subset of the REMOVE set, this will also result in this indicating that there are no operations, resulting in this being an
	 * empty set. 
	 *
	 * @return True exactly when the set of active operations is empty
	 */
	public boolean isEmpty() {
		return (this.getAddSet().isEmpty() && this.getRemSet().isEmpty()) || this.getOpsSet().isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.manager.AbstractCRDT#isCreated()
	 */
	public boolean isCreated() {
		for (AbstractOperation op : this.getOpsSet()) {
			if (op.isCreated()) {
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.manager.AbstractCRDT#isDeleted()
	 */
	public boolean isDeleted() {
		for (AbstractOperation op : this.getOpsSet()) {
			if (op.isDeleted()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This static method counts the number of AbstractOperation instances of the type given which are in the 
	 * collection provided
	 * 
	 * @param ops Collection of AbstractOperations to search for operations of the specified type
	 * @param opType Type to look for in the collection of AbstractOperations 
	 * @return Number of AbstractOperation instances with the given type
	 */
	protected static long countOperations(Collection<AbstractOperation> ops, OperationType opType) {
		long rv = 0;
		for (AbstractOperation op : ops) {
			if (opType.equals(op.getType())) {
				++rv;
			}
		}
		
		return rv;
	}

	/* (non-Javadoc)
	 * @see com.cyberfront.crdt.AbstractCRDT#countCreated()
	 */
	@Override
	public long countCreated() {
		return countOperations(this.getOpsSet(), OperationType.CREATE);
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.crdt.AbstractCRDT#countRead()
	 */
	@Override
	public long countRead() {
		return countOperations(this.getOpsSet(), OperationType.READ);
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.crdt.AbstractCRDT#countUpdate()
	 */
	@Override
	public long countUpdate() {
		return countOperations(this.getOpsSet(), OperationType.UPDATE);
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.crdt.AbstractCRDT#countDelete()
	 */
	@Override
	public long countDelete() {
		return countOperations(this.getOpsSet(), OperationType.DELETE);
	}
	
	/* (non-Javadoc)
	 * @see com.cyberfront.cmrdt.manager.AbstractCRDT#getSegment()
	 */
	@Override
	protected String getSegment() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.getSegment() + ",");
		sb.append("\"addSet\":" + Support.convert(this.getAddSet()) + ",");
		sb.append("\"remSet\":" + Support.convert(this.getRemSet()) + ",");
		sb.append("\"opSet\":" + Support.convert(this.getOpsSet()));

		return sb.toString();
	}
}
