package de.yadrone.base.command;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class CommandQueue extends PriorityBlockingQueue<ATCommand> {

	private static final AtomicLong seq = new AtomicLong(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.PriorityBlockingQueue#add(java.lang.Object)
	 */
	@Override
	public boolean add(ATCommand e) {
		e.setQorder(seq.getAndIncrement());
		return super.add(e);
	}

	public CommandQueue(int capacity) {
		super(capacity, new Comparator<ATCommand>() {

			@Override
			public int compare(final ATCommand l, final ATCommand r) {
				final Priority lp = l.getPriority();
				final Priority rp = r.getPriority();
				if (lp != rp) {
					return lp.compareTo(rp);
				}
				
				final long lo = l.getQorder();
				final long ro = r.getQorder();

				if (lo < ro) {
					return -1;
				}
				if (lo > ro) {
					return 1;
				}
				return 0;
			}
		});
	}

}
