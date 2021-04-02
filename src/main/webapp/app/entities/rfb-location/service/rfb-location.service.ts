import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRfbLocation, getRfbLocationIdentifier } from '../rfb-location.model';

export type EntityResponseType = HttpResponse<IRfbLocation>;
export type EntityArrayResponseType = HttpResponse<IRfbLocation[]>;

@Injectable({ providedIn: 'root' })
export class RfbLocationService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/rfb-locations');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(rfbLocation: IRfbLocation): Observable<EntityResponseType> {
    return this.http.post<IRfbLocation>(this.resourceUrl, rfbLocation, { observe: 'response' });
  }

  update(rfbLocation: IRfbLocation): Observable<EntityResponseType> {
    return this.http.put<IRfbLocation>(`${this.resourceUrl}/${getRfbLocationIdentifier(rfbLocation) as number}`, rfbLocation, {
      observe: 'response',
    });
  }

  partialUpdate(rfbLocation: IRfbLocation): Observable<EntityResponseType> {
    return this.http.patch<IRfbLocation>(`${this.resourceUrl}/${getRfbLocationIdentifier(rfbLocation) as number}`, rfbLocation, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRfbLocation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRfbLocation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRfbLocationToCollectionIfMissing(
    rfbLocationCollection: IRfbLocation[],
    ...rfbLocationsToCheck: (IRfbLocation | null | undefined)[]
  ): IRfbLocation[] {
    const rfbLocations: IRfbLocation[] = rfbLocationsToCheck.filter(isPresent);
    if (rfbLocations.length > 0) {
      const rfbLocationCollectionIdentifiers = rfbLocationCollection.map(rfbLocationItem => getRfbLocationIdentifier(rfbLocationItem)!);
      const rfbLocationsToAdd = rfbLocations.filter(rfbLocationItem => {
        const rfbLocationIdentifier = getRfbLocationIdentifier(rfbLocationItem);
        if (rfbLocationIdentifier == null || rfbLocationCollectionIdentifiers.includes(rfbLocationIdentifier)) {
          return false;
        }
        rfbLocationCollectionIdentifiers.push(rfbLocationIdentifier);
        return true;
      });
      return [...rfbLocationsToAdd, ...rfbLocationCollection];
    }
    return rfbLocationCollection;
  }
}
