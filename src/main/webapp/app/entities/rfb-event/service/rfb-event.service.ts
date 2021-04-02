import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRfbEvent, getRfbEventIdentifier } from '../rfb-event.model';

export type EntityResponseType = HttpResponse<IRfbEvent>;
export type EntityArrayResponseType = HttpResponse<IRfbEvent[]>;

@Injectable({ providedIn: 'root' })
export class RfbEventService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/rfb-events');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(rfbEvent: IRfbEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEvent);
    return this.http
      .post<IRfbEvent>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(rfbEvent: IRfbEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEvent);
    return this.http
      .put<IRfbEvent>(`${this.resourceUrl}/${getRfbEventIdentifier(rfbEvent) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(rfbEvent: IRfbEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEvent);
    return this.http
      .patch<IRfbEvent>(`${this.resourceUrl}/${getRfbEventIdentifier(rfbEvent) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IRfbEvent>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRfbEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRfbEventToCollectionIfMissing(rfbEventCollection: IRfbEvent[], ...rfbEventsToCheck: (IRfbEvent | null | undefined)[]): IRfbEvent[] {
    const rfbEvents: IRfbEvent[] = rfbEventsToCheck.filter(isPresent);
    if (rfbEvents.length > 0) {
      const rfbEventCollectionIdentifiers = rfbEventCollection.map(rfbEventItem => getRfbEventIdentifier(rfbEventItem)!);
      const rfbEventsToAdd = rfbEvents.filter(rfbEventItem => {
        const rfbEventIdentifier = getRfbEventIdentifier(rfbEventItem);
        if (rfbEventIdentifier == null || rfbEventCollectionIdentifiers.includes(rfbEventIdentifier)) {
          return false;
        }
        rfbEventCollectionIdentifiers.push(rfbEventIdentifier);
        return true;
      });
      return [...rfbEventsToAdd, ...rfbEventCollection];
    }
    return rfbEventCollection;
  }

  protected convertDateFromClient(rfbEvent: IRfbEvent): IRfbEvent {
    return Object.assign({}, rfbEvent, {
      eventDate: rfbEvent.eventDate?.isValid() ? rfbEvent.eventDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.eventDate = res.body.eventDate ? dayjs(res.body.eventDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((rfbEvent: IRfbEvent) => {
        rfbEvent.eventDate = rfbEvent.eventDate ? dayjs(rfbEvent.eventDate) : undefined;
      });
    }
    return res;
  }
}
