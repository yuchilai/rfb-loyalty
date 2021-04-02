import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'rfb-location',
        data: { pageTitle: 'RfbLocations' },
        loadChildren: () => import('./rfb-location/rfb-location.module').then(m => m.RfbLocationModule),
      },
      {
        path: 'rfb-event',
        data: { pageTitle: 'RfbEvents' },
        loadChildren: () => import('./rfb-event/rfb-event.module').then(m => m.RfbEventModule),
      },
      {
        path: 'rfb-event-attendance',
        data: { pageTitle: 'RfbEventAttendances' },
        loadChildren: () => import('./rfb-event-attendance/rfb-event-attendance.module').then(m => m.RfbEventAttendanceModule),
      },
      {
        path: 'rfb-user',
        data: { pageTitle: 'RfbUsers' },
        loadChildren: () => import('./rfb-user/rfb-user.module').then(m => m.RfbUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
