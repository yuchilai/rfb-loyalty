import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RfbUserDetailComponent } from './rfb-user-detail.component';

describe('Component Tests', () => {
  describe('RfbUser Management Detail Component', () => {
    let comp: RfbUserDetailComponent;
    let fixture: ComponentFixture<RfbUserDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RfbUserDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ rfbUser: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RfbUserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RfbUserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load rfbUser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.rfbUser).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
