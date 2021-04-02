import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RfbLocationDetailComponent } from './rfb-location-detail.component';

describe('Component Tests', () => {
  describe('RfbLocation Management Detail Component', () => {
    let comp: RfbLocationDetailComponent;
    let fixture: ComponentFixture<RfbLocationDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RfbLocationDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ rfbLocation: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RfbLocationDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RfbLocationDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load rfbLocation on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.rfbLocation).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
