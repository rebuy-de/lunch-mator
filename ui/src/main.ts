import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {LunchMatorModule} from './lunch-mator/lunch-mator.module';
import {environment} from './environments/environment';

if (environment.production) {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(LunchMatorModule)
    .catch(err => console.log(err));
