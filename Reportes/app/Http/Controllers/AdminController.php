<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AdminController extends Controller
{
    public function index()
    {
        if (session('logueado')) {
            dd(session('logueado'));
            return view('admin');
        }
        return view('login');
    }

    public function reporte(Request $request)
    {
        if (!session('logueado')) {
            return view('login');
        }

        $date1 = date($request->start);
        $date2 = date($request->end);
        $fecha_final = date("Y-m-d", strtotime($date2 . "+ 1 days"));

        $sql = "SELECT DATE(sales.date) AS day, SUM(total) AS total_sum
                    FROM sales 
                        WHERE sales.Date BETWEEN '$date1' AND '$date2'
                        GROUP BY DAY;";
        $data = DB::select($sql);
        //dd($data);

        $reporte = [];
        foreach ($data as $item) {
            $sql = "SELECT DATE(sales.date) AS day, concepts.name, SUM(detail.quantity) AS total_quantity, 
                    ROUND(SUM(detail.price * 1.0825), 2)  AS total_price
                        FROM sales
                            INNER JOIN sales_detail AS detail ON sales.id = detail.idsale
                            INNER JOIN concepts ON detail.idconcept = concepts.id
                                WHERE DATE(sales.date) = '$item->day'
                                GROUP BY day, concepts.name;";
            $dataDia = DB::select($sql);
            $item->day = date('F j', strtotime($item->day));
            $reporteData = [
                'reporteDia' => $item,
                'detallesDia' => $dataDia,
            ];
            $reporte[] = $reporteData;
        }

        $days = [
            'start' => date('F j Y', strtotime($request->start)),
            'end' => date('F j Y', strtotime($request->end)),
        ];

        $pdf = Pdf::loadView('reporte', compact('reporte', 'days'));

        //muestra el pdf
        return $pdf->stream();
    }

    public function stock(Request $request)
    {
        $sql = "";
        $product = $request->product;
        if ($product == -1) {
            $sql = "SELECT id,name,description,quantity,price FROM concepts where status=1;";
        } else {
            $sql = "SELECT id,name,description,quantity,price FROM concepts where id=$product and status=1;";
        }

        $data = DB::select($sql);
        $day =  date('F j Y', strtotime(date('Y-m-d')));

        $pdf = Pdf::loadView('stock', compact('data', 'day'));

        //muestra el pdf
        return $pdf->stream();
    }
}
