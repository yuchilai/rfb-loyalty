import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRfbUser, getRfbUserIdentifier } from '../rfb-user.model';

export type EntityResponseType = HttpResponse<IRfbUser>;
export type EntityArrayResponseType = HttpResponse<IRfbUser[]>;

@Injectable({ providedIn: 'root' })
export class RfbUserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/rfb-users');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(rfbUser: IRfbUser): Observable<EntityResponseType> {
    return this.http.post<IRfbUser>(this.resourceUrl, rfbUser, { observe: 'response' });
  }

  update(rfbUser: IRfbUser): Observable<EntityResponseType> {
    return this.http.put<IRfbUser>(`${this.resourceUrl}/${getRfbUserIdentifier(rfbUser) as number}`, rfbUser, { observe: 'response' });
  }

  partialUpdate(rfbUser: IRfbUser): Observable<EntityResponseType> {
    return this.http.patch<IRfbUser>(`${this.resourceUrl}/${getRfbUserIdentifier(rfbUser) as number}`, rfbUser, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRfbUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRfbUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRfbUserToCollectionIfMissing(rfbUserCollection: IRfbUser[], ...rfbUsersToCheck: (IRfbUser | null | undefined)[]): IRfbUser[] {
    const rfbUsers: IRfbUser[] = rfbUsersToCheck.filter(isPresent);
    if (rfbUsers.length > 0) {
      const rfbUserCollectionIdentifiers = rfbUserCollection.map(rfbUserItem => getRfbUserIdentifier(rfbUserItem)!);
      const rfbUsersToAdd = rfbUsers.filter(rfbUserItem => {
        const rfbUserIdentifier = getRfbUserIdentifier(rfbUserItem);
        if (rfbUserIdentifier == null || rfbUserCollectionIdentifiers.includes(rfbUserIdentifier)) {
          return false;
        }
        rfbUserCollectionIdentifiers.push(rfbUserIdentifier);
        return true;
      });
      return [...rfbUsersToAdd, ...rfbUserCollection];
    }
    return rfbUserCollection;
  }
}
