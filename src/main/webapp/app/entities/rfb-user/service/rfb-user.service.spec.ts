import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRfbUser, RfbUser } from '../rfb-user.model';

import { RfbUserService } from './rfb-user.service';

describe('Service Tests', () => {
  describe('RfbUser Service', () => {
    let service: RfbUserService;
    let httpMock: HttpTestingController;
    let elemDefault: IRfbUser;
    let expectedResult: IRfbUser | IRfbUser[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(RfbUserService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        username: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a RfbUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new RfbUser()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a RfbUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            username: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a RfbUser', () => {
        const patchObject = Object.assign({}, new RfbUser());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of RfbUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            username: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a RfbUser', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addRfbUserToCollectionIfMissing', () => {
        it('should add a RfbUser to an empty array', () => {
          const rfbUser: IRfbUser = { id: 123 };
          expectedResult = service.addRfbUserToCollectionIfMissing([], rfbUser);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbUser);
        });

        it('should not add a RfbUser to an array that contains it', () => {
          const rfbUser: IRfbUser = { id: 123 };
          const rfbUserCollection: IRfbUser[] = [
            {
              ...rfbUser,
            },
            { id: 456 },
          ];
          expectedResult = service.addRfbUserToCollectionIfMissing(rfbUserCollection, rfbUser);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a RfbUser to an array that doesn't contain it", () => {
          const rfbUser: IRfbUser = { id: 123 };
          const rfbUserCollection: IRfbUser[] = [{ id: 456 }];
          expectedResult = service.addRfbUserToCollectionIfMissing(rfbUserCollection, rfbUser);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbUser);
        });

        it('should add only unique RfbUser to an array', () => {
          const rfbUserArray: IRfbUser[] = [{ id: 123 }, { id: 456 }, { id: 48866 }];
          const rfbUserCollection: IRfbUser[] = [{ id: 123 }];
          expectedResult = service.addRfbUserToCollectionIfMissing(rfbUserCollection, ...rfbUserArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const rfbUser: IRfbUser = { id: 123 };
          const rfbUser2: IRfbUser = { id: 456 };
          expectedResult = service.addRfbUserToCollectionIfMissing([], rfbUser, rfbUser2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbUser);
          expect(expectedResult).toContain(rfbUser2);
        });

        it('should accept null and undefined values', () => {
          const rfbUser: IRfbUser = { id: 123 };
          expectedResult = service.addRfbUserToCollectionIfMissing([], null, rfbUser, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbUser);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
