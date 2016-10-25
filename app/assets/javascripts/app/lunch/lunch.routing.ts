import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LunchListComponent} from 'app/lunch/components/list/lunch-list.component';
import {AddLunchComponent} from './components/addlunch/add-lunch.component';
import {MyLunchListComponent} from './components/mylunch/lunch-list.component';

const lunchRoutes: Routes = [
  {
    path: 'lunch/list',
    component: LunchListComponent
  },
  {
    path: 'lunch/my-list',
    component: MyLunchListComponent
  },
  {
    path: 'add-lunch',
    component: AddLunchComponent
  }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(lunchRoutes);
