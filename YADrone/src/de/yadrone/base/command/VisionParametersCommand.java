package de.yadrone.base.command;

public class VisionParametersCommand extends ATCommand {

	int coarse_scale; // scale of current picture with respect to original picture
	int nb_pair; // number of searched pairs in each direction
	int loss_per; // authorized lost pairs percentage for tracking
	int nb_tracker_width; // number of trackers in width of current picture
	int nb_tracker_height; // number of trackers in height of current picture
	int scale; // distance between two pixels in a pair
	int trans_max; // largest value of trackers translation between two adjacent pictures
	int max_pair_dist; // largest distance of pairs research from tracker location
	int noise; // threshold of meaningful contrast

	public VisionParametersCommand(int coarse_scale, int nb_pair, int loss_per, int nb_tracker_width,
			int nb_tracker_height, int scale, int trans_max, int max_pair_dist, int noise) {
		super();
		this.coarse_scale = coarse_scale;
		this.nb_pair = nb_pair;
		this.loss_per = loss_per;
		this.nb_tracker_width = nb_tracker_width;
		this.nb_tracker_height = nb_tracker_height;
		this.scale = scale;
		this.trans_max = trans_max;
		this.max_pair_dist = max_pair_dist;
		this.noise = noise;
	}

	@Override
	protected String getID() {
		return "VISP";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { coarse_scale, nb_pair, loss_per, nb_tracker_width, nb_tracker_height, scale, trans_max,
				max_pair_dist, noise };
	}
}
