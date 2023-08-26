<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;

class CacheClear extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'app:cache-clear';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Optimize cache by running multiple cache-related commands';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $this->call('cache:clear');
        $this->call('view:clear');
        $this->call('route:cache');
        $this->call('config:cache');
        $this->call('optimize');

        $this->info('Cache optimization completed.');
    }
}
