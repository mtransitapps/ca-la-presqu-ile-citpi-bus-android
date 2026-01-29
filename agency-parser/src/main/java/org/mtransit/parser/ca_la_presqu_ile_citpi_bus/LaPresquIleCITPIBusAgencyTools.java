package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import static org.mtransit.commons.Constants.EMPTY;
import static org.mtransit.commons.RegexUtils.DIGITS;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.RegexUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://exo.quebec/en/about/open-data
public class LaPresquIleCITPIBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new LaPresquIleCITPIBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "exo PI";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public @Nullable String getTripIdCleanupRegex() {
		return "PI\\-\\w{1}\\d{2}\\-(PI_GTFS)\\-"; // remove trip ID shared by all trip IDs (include season letter and YY year)
	}

	@Override
	public @Nullable String getServiceIdCleanupRegex() {
		return "^PI\\-\\w{1}\\d{2}\\-(PI_GTFS)\\-"; // remove beginning of service ID shared by all service IDs (include season letter and YY year)
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final Pattern FIX_A40_ = Pattern.compile("(^40$)");
	private static final String FIX_A40_REPLACEMENT = "A40";

	@NotNull
	@Override
	public String cleanRouteShortName(@NotNull String routeShortName) {
		routeShortName = FIX_A40_.matcher(routeShortName).replaceAll(FIX_A40_REPLACEMENT);
		return super.cleanRouteShortName(routeShortName);
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@NotNull
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		return gRoute.getRouteShortName(); // used by GTFS-RT
	}

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanDirectionHeadsign(int directionId, boolean fromStopName, @NotNull String directionHeadSign) {
		if (directionHeadSign.endsWith("AM")) {
			return "AM";
		} else if (directionHeadSign.endsWith("PM")) {
			return "PM";
		}
		directionHeadSign = super.cleanDirectionHeadsign(directionId, fromStopName, directionHeadSign);
		return directionHeadSign;
	}

	private static final Pattern SERVICE = Pattern.compile("(service) ([a|p]m)", Pattern.CASE_INSENSITIVE);
	private static final String SERVICE_REPLACEMENT = "$2";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = SERVICE.matcher(tripHeadsign).replaceAll(SERVICE_REPLACEMENT);
		tripHeadsign = CleanUtils.keepToFR(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[]{START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE};

	private static final Pattern[] SPACE_FACES = new Pattern[]{SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE};

	private static final Pattern DEVANT_ = CleanUtils.cleanWordsFR("devant");

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = DEVANT_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = RegexUtils.replaceAllNN(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = RegexUtils.replaceAllNN(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
			return EMPTY;
		}
		//noinspection deprecation
		return gStop.getStopId(); // used by GTFS-RT
	}

	private static final String DDO = "DDO";
	private static final String HUD = "HUD";
	private static final String LIP = "LIP";
	private static final String NIP = "NIP";
	private static final String PCL = "PCL";
	private static final String PIN = "PIN";
	private static final String RIG = "RIG";
	private static final String SAB = "SAB";
	private static final String SGV = "SGV";
	private static final String SLR = "SLR";
	private static final String SLZ = "SLZ";
	private static final String VAU = "VAU";

	@Override
	public int getStopId(@NotNull GStop gStop) {
		final String stopCode = getStopCode(gStop);
		if (stopCode.length() > 0 && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection deprecation
		final String stopIds = CleanUtils.cleanMergedID(gStop.getStopId()).toUpperCase(Locale.ENGLISH);
		if ("PItE22".equalsIgnoreCase(stopIds)) {
			return 9_000_000;
		}
		final Matcher matcher = DIGITS.matcher(stopIds);
		if (matcher.find()) {
			final String digitS = matcher.group();
			final int digits = Integer.parseInt(digitS);
			int stopId;
			final String beforeDigits = stopIds.substring(0, stopIds.indexOf(digitS));
			switch (beforeDigits) {
			case EMPTY:
				stopId = 0;
				break;
			case DDO:
				stopId = 100_000;
				break;
			case HUD:
				stopId = 200_000;
				break;
			case LIP:
				stopId = 300_000;
				break;
			case NIP:
				stopId = 400_000;
				break;
			case PCL:
				stopId = 500_000;
				break;
			case PIN:
				stopId = 600_000;
				break;
			case RIG:
				stopId = 700_000;
				break;
			case SAB:
				stopId = 800_000;
				break;
			case SGV:
				stopId = 900_000;
				break;
			case SLR:
				stopId = 1_000_000;
				break;
			case SLZ:
				stopId = 1_100_000;
				break;
			case VAU:
				stopId = 1_200_000;
				break;
			case "MITIPI":
				stopId = 1_300_000;
				break;
			default:
				throw new MTLog.Fatal("Stop doesn't have an ID (start with)! %s!", gStop);
			}
			final String afterDigits = stopIds.substring(stopIds.indexOf(digitS) + digitS.length());
			switch (afterDigits) {
			case EMPTY:
				// stopId += 0;
				break;
			case A:
				stopId += 1_000;
				break;
			case B:
				stopId += 2_000;
				break;
			case C:
				stopId += 3_000;
				break;
			case D:
				stopId += 4_000;
				break;
			case E:
				stopId += 5_000;
				break;
			case F:
				stopId += 6_000;
				break;
			case G:
				stopId += 7_000;
				break;
			case H:
				stopId += 8_000;
				break;
			default:
				throw new MTLog.Fatal("Stop doesn't have an ID (end with)! %s!", gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
