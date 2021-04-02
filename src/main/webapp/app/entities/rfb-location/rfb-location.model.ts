import { IRfbEvent } from 'app/entities/rfb-event/rfb-event.model';

export interface IRfbLocation {
  id?: number;
  locationName?: string | null;
  runDayOfWeek?: number | null;
  rvbEvents?: IRfbEvent[] | null;
}

export class RfbLocation implements IRfbLocation {
  constructor(
    public id?: number,
    public locationName?: string | null,
    public runDayOfWeek?: number | null,
    public rvbEvents?: IRfbEvent[] | null
  ) {}
}

export function getRfbLocationIdentifier(rfbLocation: IRfbLocation): number | undefined {
  return rfbLocation.id;
}
