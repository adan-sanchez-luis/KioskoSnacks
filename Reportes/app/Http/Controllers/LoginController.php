<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

use function Laravel\Prompts\error;

class LoginController extends Controller
{
    public function index()
    {
        session(['logueado' => false]);
        return view("login");
    }

    public function login(Request $request)
    {
        $request->validate([
            'user' => 'required',
            'password' => 'required',
            'code' => 'required',
        ], [
            'user.required' => 'User name is required',
            'password.required' => 'Password is required',
            'code.required' => 'App code is required',
        ]);

        $user = $request->user;
        $password = $request->password;
        $code = $request->code;

        $datos = DB::selectOne("SELECT * from dbpracticas.app_access where UserName = '$user' and Password='$password' and AppName = '$code' limit 1;");

        if (is_null($datos)) {
            return back()->withErrors(['message' => 'User or password incorrect']);
        }

        session(['logueado' => true]);

        $fecha_actual = date('Y-m-d');
        $max = date("Y-m-d", strtotime($fecha_actual . "- 1 days"));
        $min = date("Y-m-d", strtotime($fecha_actual . "- 7 days"));

        $data = [
            'max' => $max,
            'min' => $min,
            'actual' => $fecha_actual,
        ];

        $products = DB::select("SELECT id,name from concepts where status=1;");

        return view('admin', compact('data', 'products'));
    }

    public function logout(Request $request)
    {
        session(['logueado' => false]);
        return view("login");
    }
}
