/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
/*
 * Copyright (C) 2004,
 *
 * Arjuna Technologies Ltd,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: LocalCleanupSynchronization.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.internal.jta.resources.jts;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.SynchronizationRecord;
import com.arjuna.ats.internal.jta.transaction.jts.TransactionImple;

/**
 * This synchronization is responsible for removing the JTA transaction
 * from the internal table. We don't need one for the purely local JTA
 * implementation, since the transaction implementation will do this
 * itself. However, in the JTS implementation, where a subordinate JTA
 * transaction may be proxied in another JVM, we have to rely on the
 * synchronization to do the garbage collection, since that transaction
 * implementation won't be driven through commit or rollback - it'll go
 * through the 2PC methods at the JTS interposition hierarchy level.
 */

public class LocalCleanupSynchronization implements com.arjuna.ats.arjuna.coordinator.SynchronizationRecord
{
    public LocalCleanupSynchronization (TransactionImple tx)
    {
	_tx = tx;
    }

    public boolean beforeCompletion ()
    {
	return true;
    }

    /**
     * status is ActionStatus
     */
    
    public boolean afterCompletion (int status)
    {
	_tx.shutdown();

	return true;
    }

    @Override
    public boolean isInterposed() {
        return false;
    }

    public Uid get_uid ()
    {
	return _theUid;
    }

    private TransactionImple _tx;
    private Uid              _theUid = new Uid();

    public int compareTo(Object o) {
        SynchronizationRecord sr = (SynchronizationRecord)o;
        if(_theUid.equals(sr.get_uid())) {
            return 0;
        } else {
            return _theUid.lessThan(sr.get_uid()) ? -1 : 1;
        }
    }

}
