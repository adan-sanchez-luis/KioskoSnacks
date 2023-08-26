<?php

use App\Http\Controllers\AdminController;
use App\Http\Controllers\LoginController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

Route::get('/', [LoginController::class,"index"])->name('index');
Route::post('/', [LoginController::class,"login"])->name('signin');
Route::get('/logout', [LoginController::class,"logout"])->name('logout');
/*
Route::get('/', function(){
    return view('admin');
});*/

Route::prefix('admin')->group(function () {
    Route::view('/', [AdminController::class, 'index'])->name('admin');
    Route::post('/stock', [AdminController::class, 'stock'])->name('stock.pdf');
    Route::post('/reporte', [AdminController::class, 'reporte'])->name('reporte.pdf');
    // Otras rutas protegidas con el prefijo 'admin'
});